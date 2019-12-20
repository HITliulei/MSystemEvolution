package com.ll.service.utils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.ll.service.bean.MPathInfo;
import com.septemberhx.common.service.MParamer;
import com.septemberhx.common.service.MService;
import com.septemberhx.common.service.MServiceInterface;
import com.septemberhx.common.service.MServiceVersion;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * Created by Lei on 2019/11/29 15:45
 */
public class GetServiceInfo {

    public static MService getMservice(String version, MPathInfo pathInfo){
        MService mService = getConfig(pathInfo.getApplication_Path());
        MServiceVersion mServiceVersion = new MServiceVersion();
        String[] versions =  version.replaceAll("[a-zA-Z]","").split("\\.");
        mServiceVersion.setMainVersionNum(Integer.parseInt(versions[0]));
        mServiceVersion.setChildVersionNum(Integer.parseInt(versions[1]));
        mServiceVersion.setFixVersionNum(Integer.parseInt(versions[2]));
        mService.setServiceVersion(mServiceVersion);
        Map<String, MServiceInterface> map = new HashMap<>();
        for(String s : pathInfo.getController_ListPath()){
            map.putAll(getServiceInfo(s));
        }
        mService.setServiceInterfaceMap(map);
        return mService;
    }

    public static MService getConfig(String path){
        String[] paths = path.split("\\.");
        if(paths[paths.length-1].equals("yml")){
            return getInfoFromyml(path);
        }else{
            return getInfoFromproperties(path);
        }
    }
    public static MService getInfoFromyml(String path) {
        MService mService = new MService();
        Yaml yaml = new Yaml();
        InputStream inputStream = GetServiceInfo.class.getClassLoader().getResourceAsStream(path);
        Map obj = yaml.loadAs(inputStream, LinkedHashMap.class);
        Map server = (Map) obj.get("server");
        if(server.get("port") == null){
            mService.setPort(8080);
        }else{
            mService.setPort((int)server.get("port"));
        }
        if(server.get("servlet") == null){
            mService.setGirUrl("/");
        }else{
            mService.setGirUrl(((Map)server.get("servlet")).get("context-path").toString());
        }
        Map spring = (Map) obj.get("spring");
        mService.setServiceName(((Map)spring.get("application")).get("name").toString());
        return mService;
    }
    public static MService getInfoFromproperties(String path){
        MService mService = new MService();
        Properties properties = new Properties();
        try{
            properties.load(new FileInputStream("src/main/resources/" + path));
            if(properties.get("server.port") == null){
                mService.setPort(8080);
            }else{
                mService.setPort(Integer.parseInt(properties.getProperty("server.port")));
            }
            mService.setServiceName(properties.getProperty("spring.application.name"));
            if(properties.getProperty("server.context-path") == null){
                mService.setGirUrl("/");
            }else{
                mService.setGirUrl(properties.getProperty("server.context-path"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mService;
    }


    /**
     * 根据路径得到接口信息
     * @return Service类，返回该类的信息
     */
    public static Map<String, MServiceInterface> getServiceInfo(String codepath){
        CompilationUnit compilationUnit = null;
        File file;
            try{
                file = new File(codepath);
            if(!file.exists()){
                System.out.println("源代码路径不对");
            }else{
                compilationUnit =  JavaParser.parse(file);
            }
        }catch (Exception e){
            System.out.println("读取code路径失败");
            return null;
        }
        String[] strings = codepath.split("/");
        String className = strings[strings.length-1].split("\\.")[0];
        ClassOrInterfaceDeclaration c = compilationUnit.getClassByName(className).get();
        NodeList<AnnotationExpr> annotations = c.getAnnotations();

        List<String> pathContexts = new ArrayList<>();   // 所有的基础路径
        /*得到服务注释方面*/
        Map<String, MServiceInterface> map = new HashMap<>();
        for (AnnotationExpr annotationExpr : annotations){
            List<Node> childNodes = annotationExpr.getChildNodes();
            String annoName = childNodes.get(0).toString();
            if(annoName.equals("RequestMapping")){  // 在此得到路径信息
                Node s = childNodes.get(1);
                if(s.getChildNodes().size() == 0){
                    String h = s.toString();
                    pathContexts.add(h.substring(1,h.length()-1));
                }else{
                    Node node1 = s.getChildNodes().get(1);
                    if(node1.getChildNodes().size() ==0){
                        String h = node1.toString();
                        pathContexts.add(h.substring(1,h.length()-1));
                    }else{
                        for(Node node:node1.getChildNodes()){
                            String h = node.toString();
                            pathContexts.add(h.substring(1,h.length()-1));
                        }
                    }
                }
            }
        }
        if(pathContexts.size() == 0){
            pathContexts.add("/");
        }
        /*得到interface方面*/
        List<MethodDeclaration> methodDeclarationList = c.getMethods();

        for(MethodDeclaration m:methodDeclarationList){   // 遍历每一个方法
            MServiceInterface mServiceInterface = new MServiceInterface();
            mServiceInterface.setFunctionName(m.getName().toString());  //  函数名称
            mServiceInterface.setReturnType(m.getType().toString());   //返回值
            List<String> pathurl = new ArrayList<>();  // interface的url
            NodeList<AnnotationExpr> anno = m.getAnnotations();
            List<String> pathContexts_function = new ArrayList<>();
            for (AnnotationExpr annotationExpr:anno){  // 注解
                List<Node> childNodes = annotationExpr.getChildNodes();
                String annoName =childNodes.get(0).toString();
                if(annoName.equals("RequestMapping") || annoName.equals("GetMapping") || annoName.equals("PostMapping") || annoName.equals("DeleteMapping") ||annoName.equals("PutMapping")){
                    Node s = childNodes.get(1);
                    List<Node> s_url = s.getChildNodes();
                    if(s_url.size() == 0){
                        String h = s.toString();
                        pathContexts_function.add(h.substring(1,h.length()-1));
                    }else{
                        Node node1 = s_url.get(1);
                        if(node1.getChildNodes().size() ==0){
                            String h = node1.toString();
                            pathContexts_function.add(h.substring(1,h.length()-1));
                        }else{
                            for(Node node:node1.getChildNodes()){
                                String h = node.toString();
                                pathContexts_function.add(h.substring(1,h.length()-1));
                            }
                        }
                    }
                }
                for(String string1 :pathContexts){
                    for(String string2: pathContexts_function){
                        if(!string1.contains("/")){
                            pathurl.add(string1+"/"+string2);
                        }else{
                            pathurl.add(string1+string2);
                        }
                    }
                }
                if(annoName.equals("RequestMapping")){  // 方法名称
                    if(childNodes.size() == 2){                      //Request有对方法的描述
                        mServiceInterface.setRequestMethod("RequestMethod");
                    }else{
                        String[] requestmethods = childNodes.get(2).toString().split("=");
                        String requestmethod = requestmethods[1].trim();
                        mServiceInterface.setRequestMethod(requestmethod);
                    }
                }else if(annoName.equals("GetMapping")){
                    mServiceInterface.setRequestMethod(" RequestMethod.GET");
                }else if(annoName.equals("PostMapping")){
                    mServiceInterface.setRequestMethod(" RequestMethod.POST");
                }else if(annoName.equals("DeleteMapping")){
                    mServiceInterface.setRequestMethod("RequestMethod.DELETE");
                }else if(annoName.equals("PutMapping")){
                    mServiceInterface.setRequestMethod("RequestMethod.PUT");
                }
            }
            if(pathurl.size() == 0){   // 判断不是rest接口类
                continue;
            }
            /*获取  接口层级的参数*/
            NodeList<Parameter> parameters = m.getParameters();
            List<MParamer> paramerList = new ArrayList<>();
            for(Parameter parameter :parameters){
                MParamer paramer = new MParamer();
                List<Node> childNodes = parameter.getChildNodes();
                paramer.setName(childNodes.get(2).toString());  // 参数名称
                paramer.setType(childNodes.get(1).toString());  // 参数类型
                Node node = childNodes.get(0);
                List<Node> annoInfo = node.getChildNodes();
                String method =annoInfo.get(0).toString();
                if(method.equals("RequestBody")){   // requestBody方式单独列出来
                    paramer.setMethod(method);
                    paramer.setRequestname("实体类");
                    paramer.setDefaultObject("");
                }else{
                    paramer.setMethod(method);  // 参数的请求方式
                    String name = annoInfo.get(1).toString();
                    if(annoInfo.size() == 2){
                        String trueName = name.trim().replace("\"","");
                        paramer.setRequestname(trueName);
                        paramer.setDefaultObject("");
                    }else{
                        String trueName = name.split("=")[1].trim().replace("\"","");
                        String defauleValue = annoInfo.get(2).toString().split("=")[1].trim().replace("\"","");
                        paramer.setRequestname(trueName);
                        paramer.setDefaultObject(defauleValue);
                    }
                }
                paramerList.add(paramer);
            }
            mServiceInterface.setParamers(paramerList);
            for(String string : pathurl){
                mServiceInterface.setPatternUrl(string);
                map.put(string,mServiceInterface);
            }
        }
        return map;
    }


    public static void main(String[] args) {
        System.out.println(getConfig("workplace/com-hitices-multiversion-test_v1.0.3/src/main/resources/application.yml"));
    }
}
