pipeline {
    agent any

   tools {
        maven 'MVN3'
        jdk 'JDK25'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/amantiwari8861/AutomationFrameworkSeleniumCICD'
            }
        }

        stage('Run Tests') {
            steps {
                withCredentials([
                    file(credentialsId: 'automation-config',
                         variable: 'CONFIG_FILE')
                ]) {
                    sh '''
                        cp "$CONFIG_FILE" src/main/resources/config.properties
                        mvn clean test
                    '''
                }
            }
        }
    }

    post {
        always {
            sh 'rm -f src/main/resources/config.properties'
        }
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
    }
}