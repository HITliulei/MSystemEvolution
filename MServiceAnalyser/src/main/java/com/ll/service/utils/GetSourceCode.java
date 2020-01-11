package com.ll.service.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.ll.service.bean.MPathInfo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lei on 2019/11/29 15:45
 */

public class GetSourceCode {

    public final static String CODE_DIWNLOAD_PATH = "/Workplace/test";

    /**
     * 下载源码 并得到路径信息
     */
    public static Map<String, MPathInfo> getCodeAndGetMPathInfo(String url) {
        String workplace = CODE_DIWNLOAD_PATH;
        String[] urls = url.split("/");
        String projectName = urls[urls.length - 1].split("\\.")[0];
        List<String> allTags = getAllTags(url, workplace, projectName);
        Map<String, MPathInfo> map = new HashMap<>();
        for (String tag : allTags) {
            try {
                Git.cloneRepository().setURI(url).setBranch(tag).setDirectory(new File(workplace + "/" + projectName + "_" + tag)).call();
                MPathInfo mPathInfo = getMPathInfo(tag, workplace, projectName);
                map.put(tag, mPathInfo);
            } catch (GitAPIException g) {
                System.out.println("下载版本代码失败");
                g.printStackTrace();
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
        String workplace = CODE_DIWNLOAD_PATH;
        String[] urls = url.split("/");
        String projectName = urls[urls.length - 1].split("\\.")[0];
        deleteWorkplace(workplace + "/" + projectName + "_" + version);
        MPathInfo mPathInfo = null;
        File file = new File(workplace + "/" + projectName + "_" + version);
        if (file.exists()) {
            mPathInfo = getMPathInfo(version, workplace, projectName);
        } else {
            try {
                Git.cloneRepository().setURI(url).setBranch(version).setDirectory(file).call();
                mPathInfo = getMPathInfo(version, workplace, projectName);
            } catch (GitAPIException g) {
                System.out.println("下载版本代码失败");
                g.printStackTrace();
            }
        }
        return mPathInfo;
    }


    /**
     * get controller path info
     *
     * @param version     version
     * @param workPlace   workplace path
     * @param projectName projectName
     * @return path info(contains controller)
     */
    public static MPathInfo getMPathInfo(String version, String workPlace, String projectName) {
        String path = workPlace + "/" + projectName + "_" + version + "/";
        MPathInfo MPathInfo = new MPathInfo();
        File file_findapplication = new File(path + "src/main/resources");
        String version1_ymlconfig = path + "/src/main/resources/" + getYmlPath(file_findapplication);
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


    public static boolean ifController(File file) {
        CompilationUnit compilationUnit = null;
        try {
            compilationUnit = JavaParser.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
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
     * 遍历得到
     *
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
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            System.out.println("文件删除失败,请检查文件路径是否正确");
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

    public static List<String> getAllTags(String url, String workplace, String projectname) {
        deleteWorkplace(workplace);
        String p = workplace + "/" + projectname;
        Git git = null;
        try {
            git = Git.cloneRepository().setURI(url).setDirectory(new File(p)).call();
        } catch (Exception e) {
            System.out.println("url 地址错误");
        }
        git.close();
        return new ArrayList<>(git.getRepository().getTags().keySet());
    }
}
