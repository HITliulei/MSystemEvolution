package com.septemberhx.common.build;

/**
 * @Author Lei
 * @Date 2020/2/13 23:12
 * @Version 1.0
 */
public class XmlTemplate {

    public static String getPipeline(String imagesName, String tag, String projectName, String gitUrl) {
        String pipeline = "pipeline {\n" +
                "    environment\n" +
                "    agent any\n" +
                "    stages {\n" +
                "        stage('cloning code') {\n" +
                "            steps {\n" +
                "                sh \"rm -rf *\"\n" +
                "                sh \"git clone -b \"+tag+\" \"+giturl\n" +
                "                sh \" mv \"+projectname+\"/* ./\"\n" +
                "                sh \"rm -rf \"+projectname+\"/\"\n" +
                "            }\n" +
                "        }\n" +
                "        stage(\"maven package\"){\n" +
                "            steps{\n" +
                "                sh \"mvn clean package\"\n" +
                "            }\n" +
                "        }\n" +
                "        stage('docker_images build'){\n" +
                "            steps{\n" +
                "                script{\n" +
                "                    dockerImage = docker.build(registry+':'+tag)\n" +
                "                }\n" +
                "            }\n" +
                "        }\n" +
                "        stage('docker push'){\n" +
                "            steps{\n" +
                "                sh \"docker login --username=micheallei --password=1119016521ll.\"\n" +
                "                sh \"docker tag \"+registry+\":\"+tag+\" micheallei/\"+registry+\":\"+tag\n" +
                "                sh \"docker push micheallei/\"+registry+\":\"+tag\n" +
                "                sh \"docker logout\"\n" +
                "                sh \"docker rmi \"+registry+\":\"+tag\n" +
                "                sh \"docker rmi micheallei/\"+registry+\":\"+tag\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";
        String environment = "environment {\n        " +
                "registry ='" + imagesName.toLowerCase() + "'\n        " +
                "giturl='" + gitUrl + "'\n        " +
                "tag='" + tag + "'\n        " +
                "projectname='" + projectName + "'\n    }";
        return pipeline.replaceFirst("environment", environment);
    }

    public static String getXml(String imageName, String tag, String projectName, String gitUrl) {
        String pipeline = getPipeline(imageName, tag, projectName, gitUrl);
        String xml = "<flow-definition plugin=\"workflow-job@2.32\">\n" +
                "  <actions>\n" +
                "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction plugin=\"pipeline-model-definition@1.3.7\" />\n" +
                "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction plugin=\"pipeline-model-definition@1.3.7\">\n" +
                "      <jobProperties />\n" +
                "      <triggers />\n" +
                "      <parameters />\n" +
                "      <options />\n" +
                "    </org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction>\n" +
                "  </actions>\n" +
                "  <description />\n" +
                "  <keepDependencies>false</keepDependencies>\n" +
                "  <definition class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\" plugin=\"workflow-cps@2.64\">\n" +
                "    <script>" + pipeline + "</script>\n" +
                "    <sandbox>true</sandbox>\n" +
                "  </definition>\n" +
                "  <triggers />\n" +
                "  <disabled>false</disabled>\n" +
                "</flow-definition>";
        return xml;
    }
}
