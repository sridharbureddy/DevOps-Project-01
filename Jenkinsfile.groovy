def registry = 'https://trialwf69ge.jfrog.io'
pipeline{
    agent any 
    
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-amazon-corretto'
        MAVEN_HOME = '/opt/maven'
    }

    stages {
        stage('checkout') {
            steps {
               git branch: 'test', url: 'https://github.com/sridharbureddy/DevOps-Project-01.git', credentialsId: 'github_cred_01'
            }
        }

        stage('build') {
            steps {
                script {
                    sh "${MAVEN_HOME}/bin/mvn clean install"
                }
            
            }
        }
     
        stage("war Publish") {
            steps {
                script {
                        echo '<--------------- war Publish Started --------------->'
                        def server = Artifactory.newServer url:registry+"/artifactory" ,  credentialsId:"artifact-cred"
                        def properties = "buildid=${env.BUILD_ID},commitid=${GIT_COMMIT}";
                        def uploadSpec = """{
                            "files": [
                                {
                                "pattern": "target/*.war",
                                "target": "petclinic-libs-release-local/{1}",
                                "flat": "false",
                                "props" : "${properties}",
                                "exclusions": [ "*.sha1", "*.md5"]
                                }
                            ]
                        }"""
                        def buildInfo = server.upload(uploadSpec)
                        buildInfo.env.collect()
                        server.publishBuildInfo(buildInfo)
                        echo '<--------------- war Publish Ended --------------->'  
            
            }
        }   
    }      
    }
}