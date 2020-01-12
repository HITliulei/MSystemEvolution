package com.ll.service.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.ll.service.bean.MPathInfo;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lei on 2019/11/29 15:45
 */

public class GetSourceCode {

    private static Logger logger = LogManager.getLogger(GetSourceCode.class);

    private final static String CODE_DIWNLOAD_PATH  = "/tmp/MServiceAnalyzer";


    /**
     * 下载源码 并得到路径信息
     */
    public static Map<String, MPathInfo> getCodeAndGetMPathInfo(String url) {
        String[] urls = url.split("/");
        String projectName = urls[urls.length - 1].split("\\.")[0];
        List<String> allTags = getAllTags(url, projectName);
        Map<String, MPathInfo> map = new HashMap<>();
        for (String tag : allTags) {
            try {
                Git.cloneRepository().setURI(url).setBranch(tag).setDirectory(new File(CODE_DIWNLOAD_PATH + "/" + projectName + "_" + tag)).call();
                MPathInfo mPathInfo = getMPathInfo(tag, projectName);
                mPathInfo.setGitUrl(url);
                map.put(tag, mPathInfo);
            } catch (GitAPIException g) {
                logger.error(g);
            }
        }
        return map;
    }

    /**
     * @param url
     * @param version
     * @return
     */
    public static MPathInfo getCodeByVersion(String url, String version) {
        String[] urls = url.split("/");
        String projectName = urls[urls.length - 1].split("\\.")[0];
        MPathInfo mPathInfo = null;
        File file = new File(CODE_DIWNLOAD_PATH + "/" + projectName + "_" + version);
        if (file.exists()) {
            mPathInfo = getMPathInfo(version, projectName);
        } else {
            try {
                Git.cloneRepository().setURI(url).setBranch(version).setDirectory(file).call();
                mPathInfo = getMPathInfo(version, projectName);
            } catch (GitAPIException g) {
                logger.error(g);
            }
        }
        return mPathInfo;
    }


    /**
     * get controller path info
     * @param version     version
     * @param projectName projectName
     * @return path info(contains controller)
     */
    public static MPathInfo getMPathInfo(String version, String projectName) {
        String path = CODE_DIWNLOAD_PATH + "/" + projectName + "_" + version + "/";
        MPathInfo MPathInfo = new MPathInfo();
        File file_findapplication = new File(path + "src/main/resources");
        String version1_ymlconfig = path + "src/main/resources/" + getYmlPath(file_findapplication);
        MPathInfo.setApplicationPath(version1_ymlconfig);
        List<File> pathList = getListFiles(new File(path + "src/main/java"));
        List<String> listPath = new ArrayList<>();
        for (File file : pathList) {
            if (ifController(file)) {
                listPath.add(file.toString().replace("\\", "/"));
            }
        }
        MPathInfo.setControllerListPath(listPath);
        return MPathInfo;
    }


    /**
     * 判断文件是否是由restcontroller注解的对外提供rest接口的类
     * @param file 文件
     * @return true or false
     */
    public static boolean ifController(File file) {
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = JavaParser.parse(file);
        } catch (Exception e) {
            logger.error(e);
        }
        String[] strings = file.toString().split("\\\\");
        String className = strings[strings.length - 1].split("\\.")[0];
        if (compilationUnit.getClassByName(className).isPresent()) {
            ClassOrInterfaceDeclaration c = compilationUnit.getClassByName(className).get();
            NodeList<AnnotationExpr> annotations = c.getAnnotations();
            for (Node node : annotations) {
                if ("RestController".equals(node.getChildNodes().get(0).toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 遍历得到配置文件
     * @param file
     * @return
     */
    public static String getYmlPath(File file) {
        File[] files = file.listFiles();
        for (File file1 : files) {
            if (file1.isFile()) {
                if ("application.yml".equals(file1.getName())) {
                    return "application.yml";
                } else if ("application.properties".equals(file1.getName())) {
                    return "application.properties";
                } else {
                    continue;
                }
            } else if (file1.isDirectory()) {
                return file1.getName() + "/" + getYmlPath(file1);
            } else {
                continue;
            }
        }
        return "";
    }

    /**
     * 得到一个文件夹下的所有文件
     * @param directory 目录
     * @return 文件
     */
    public static List<File> getListFiles(File directory) {
        List<File> files = new ArrayList<>();
        if (directory.isFile()) {
            files.add(directory);
            return files;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                files.addAll(getListFiles(fileOne));

            }
        }
        return files;
    }

    /**
     * 删除工作目录下的所有文件，在git clone之前
     */
    public static void deleteWorkplace(String workPlace) {
        File file = new File(workPlace);
        deleteFile(file);
    }

    /**
     * 遍历删除文件
     * @param file
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            logger.debug ("文件删除失败,请检查文件路径是否正确");
            return;
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                deleteFile(f);
            } else {
                f.delete();
            }
        }
        file.delete();
    }

    /**
     *
     * @param url 仓库地址
     * @param projectname 项目名称
     * @return
     */
    public static List<String> getAllTags(String url, String projectname) {
        deleteWorkplace(CODE_DIWNLOAD_PATH);
        String p = CODE_DIWNLOAD_PATH + "/" + projectname;
        Git git = null;
        try {
            git = Git.cloneRepository().setURI(url).setDirectory(new File(p)).call();
        } catch (Exception e) {
            logger.error(e);
        }
        git.close();
        return new ArrayList<>(git.getRepository().getTags().keySet());
    }
}
