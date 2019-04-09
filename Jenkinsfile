node {
   def mvnHome
   stage('Preparation') { // for display purposes
      // Get some code from a GitHub repository
      
      
    dir('iml') {
        git url:'git@husky:hermes/iml.git', branch:'develop'
    }
    dir('iml-generators') {
        git 'git@husky:hermes/iml-generators.git'
    }
      // Get the Maven tool.
      // ** NOTE: This 'M3' Maven tool must be configured
      // **       in the global configuration.           
      mvnHome = tool 'MAVEN3'
   }
   stage('Build Dependencies') {
       gitlabCommitStatus {
          // Run the maven build
          sh "'${mvnHome}/bin/mvn' clean package -f ./iml/releng/com.utc.utrc.hermes.iml.parent/pom.xml"
       }
   }
   stage('Build') {
       gitlabCommitStatus {
          // Run the maven build
          sh "'${mvnHome}/bin/mvn' clean package -f ./iml-generators/releng/com.utc.utrc.hermes.iml.gen.parent/pom.xml"
       }
   }
   try{
        stage('Test') {
            gitlabCommitStatus {
                wrap([$class: 'Xvfb', autoDisplayName: true, debug: true, displayNameOffset: 100, screen: '1920x1080x24']) {
                    sh "'${mvnHome}/bin/mvn' clean verify -Pjacoco -f ./iml-generators/releng/com.utc.utrc.hermes.iml.gen.parent/pom.xml"
                }
            }
        }
    }finally {
        junit '**/surefire-reports/*.xml'
    }    
    stage('SonarQube analysis') {
        withSonarQubeEnv('SonarQube') {
        // requires SonarQube Scanner for Maven 3.2+
            wrap([$class: 'Xvfb', autoDisplayName: true, debug: true, displayNameOffset: 100, screen: '1920x1080x24']) {
                sh "echo $DISPLAY"
                sh "'${mvnHome}/bin/mvn' clean verify -Pjacoco sonar:sonar -f ./iml-generators/releng/com.utc.utrc.hermes.iml.gen.parent/pom.xml"
            }
        }
    }
}