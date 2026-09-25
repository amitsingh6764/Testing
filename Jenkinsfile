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

                    echo "================================"
                    echo "Generated JAR"
                    echo "================================"

                    ls -lh target/*.jar
                '''
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'sonar-token',
                        variable: 'SONAR_TOKEN'
                    )
                ]) {
                    sh '''
                        export PATH="$JAVA_HOME/bin:$PATH"

                        echo "================================"
                        echo "SonarCloud Analysis"
                        echo "================================"

                        mvn sonar:sonar \
                            -Dsonar.host.url=https://sonarcloud.io \
                            -Dsonar.token="$SONAR_TOKEN" \
                            -Dsonar.scanner.skipJreProvisioning=true
                    '''
                }
            }
        }

        stage('SonarCloud Quality Gate') {
            steps {
                echo 'Waiting for SonarCloud Quality Gate...'

                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Manager Approval') {
            steps {
                input(
                    message: 'SonarCloud Quality Gate passed. Approve deployment?',
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

                    if [ ! -f target/testing.jar ]; then
                        echo "ERROR: target/testing.jar not found"
                        echo "Available JAR files:"
                        ls -lh target/*.jar || true
                        exit 1
                    fi

                    cp target/testing.jar "$DEPLOY_DIR/testing.jar"

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