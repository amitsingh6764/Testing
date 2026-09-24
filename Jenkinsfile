pipeline {

    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk'
        DEPLOY_DIR = '/home/amit/app'
    }

    stages {

        stage('Check Java & Maven') {
            steps {
                sh '''
                    export PATH="$JAVA_HOME/bin:$PATH"

                    echo "================================"
                    echo "Java Version"
                    echo "================================"
                    java -version

                    echo "================================"
                    echo "Javac Version"
                    echo "================================"
                    javac -version

                    echo "================================"
                    echo "Maven Version"
                    echo "================================"
                    mvn -version
                '''
            }
        }

        stage('Build') {
            steps {
                sh '''
                    export PATH="$JAVA_HOME/bin:$PATH"

                    echo "================================"
                    echo "Maven Build"
                    echo "================================"

                    mvn clean package
                '''
            }
        }

        stage('SonarQube Cloud Analysis') {
            steps {
                script {

                    def scannerHome = tool 'SonarScanner'

                    withCredentials([
                        string(
                            credentialsId: 'sonar-token',
                            variable: 'SONAR_TOKEN'
                        )
                    ]) {

                        sh """
                            export PATH="$JAVA_HOME/bin:\\$PATH"

                            echo "================================"
                            echo "SonarQube Cloud Analysis"
                            echo "================================"

                            ${scannerHome}/bin/sonar-scanner \
                                -Dsonar.organization=amitsingh6764 \
                                -Dsonar.projectKey=amitsingh6764_Testing \
                                -Dsonar.projectName=Testing \
                                -Dsonar.sources=src/main/java \
                                -Dsonar.java.binaries=target/classes \
                                -Dsonar.host.url=https://sonarcloud.io \
                                -Dsonar.scanner.skipJreProvisioning=true

                        """
                    }
                }
            }
        }

        stage('SonarQube Quality Gate') {
            steps {

                echo 'Waiting for SonarQube Cloud Quality Gate...'

                timeout(time: 5, unit: 'MINUTES') {

                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Manager Approval') {
            steps {

                input(
                    message: 'SonarQube Quality Gate passed. Approve deployment?',
                    ok: 'Approve',
                    submitter: 'manager',
                    submitterParameter: 'APPROVED_BY'
                )
            }
        }

        stage('Deploy JAR') {
            steps {

                sh '''
                    echo "================================"
                    echo "Deploying Application"
                    echo "================================"

                    mkdir -p "$DEPLOY_DIR"

                    cp target/*.jar "$DEPLOY_DIR/testing.jar"

                    echo "================================"
                    echo "Deployment Successful"
                    echo "================================"

                    ls -lh "$DEPLOY_DIR/testing.jar"
                '''
            }
        }
    }

    post {

        success {
            echo 'Pipeline completed successfully.'
            echo 'JAR has been deployed.'
        }

        failure {
            echo 'Pipeline failed.'
            echo 'JAR was NOT deployed.'
        }

        aborted {
            echo 'Pipeline was aborted.'
            echo 'JAR was NOT deployed.'
        }
    }
}