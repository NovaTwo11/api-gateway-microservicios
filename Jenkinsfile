pipeline {
    agent any

    environment {
        DOCKER_IMAGE = "api-gateway"
        SONAR_PROJECT_KEY = "api-gateway"
        MAVEN_OPTS = "-Dmaven.repo.local=.m2/repository"
    }

    tools {
        maven 'Maven-3.9'
        jdk 'JDK17'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(
                            execPattern: '**/target/jacoco.exec',
                            classPattern: '**/target/classes',
                            sourcePattern: '**/src/main/java'
                    )
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv('SonarQube') {
                        sh """
                            mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.host.url=\$SONAR_HOST_URL \
                            -Dsonar.login=\$SONAR_AUTH_TOKEN \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'HOURS') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} ."
                sh "docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Integration Tests') {
            steps {
                sh '''
                    docker-compose -f docker-compose.test.yml up -d
                    sleep 15
                    mvn verify -Pintegration-tests
                '''
            }
            post {
                always {
                    sh 'docker-compose -f docker-compose.test.yml down'
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }

        stage('Automated Tests') {
            steps {
                dir('automated-tests') {
                    sh '''
                        mvn clean test -Dcucumber.options="--tags @smoke"
                    '''
                }
            }
            post {
                always {
                    cucumber fileIncludePattern: '**/cucumber-report.json',
                            sortingMethod: 'ALPHABETICAL'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline ejecutado exitosamente!'
        }
        failure {
            echo 'Pipeline falló. Revisar logs.'
        }
    }
}