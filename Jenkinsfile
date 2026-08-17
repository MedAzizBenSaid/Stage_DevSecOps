pipeline {
    agent any

    stages {

        stage('Checkout GitHub') {
            steps {
                git(
                    url: 'https://github.com/MedAzizBenSaid/Stage_DevSecOps.git',
                    branch: 'main',
                    credentialsId: 'github-credentials'
                )
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'node --version'
                    sh 'npm --version'
                    sh 'npm ci'
                    sh 'npm run build'
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    withEnv([
                        'JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64',
                        'PATH+JAVA=/usr/lib/jvm/java-17-openjdk-amd64/bin'
                    ]) {
                        sh 'java -version'
                        sh 'javac -version'
                        sh 'mvn -version'
                        sh 'mvn clean package -DskipTests'
                    }
                }
            }
        }
        stage('Analyse SonarQube') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    withCredentials([string(
                        credentialsId: 'sonarqube-token',
                        variable: 'SONAR_TOKEN'
                    )]) {
                        sh '''
                            /opt/sonar-scanner/bin/sonar-scanner \
                            -Dsonar.token="$SONAR_TOKEN"
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        stage('Création images Docker') {
            steps {
                sh 'docker build -t student-management-frontend:latest ./frontend'
                sh 'docker build -t student-management-backend:latest ./backend'
            }
        }

        stage('Tag des images Docker') {
            steps {
                sh '''
                    docker tag student-management-frontend:latest \
                    172.20.10.5/student-management-frontend:latest

                    docker tag student-management-backend:latest \
                    172.20.10.5/student-management-backend:latest
                '''
            }
        }
        stage('Scan Trivy - Frontend') {
            steps {
                sh '''
                    echo "===== Scan sécurité Frontend ====="

                    trivy image \
                      --timeout 30m \
                      --severity HIGH,CRITICAL \
                      --exit-code 1 \
                      172.20.10.5:8082/student-management-frontend:latest
                '''
            }
        }

        stage('Scan Trivy - Backend') {
            steps {
                sh '''
                    echo "===== Scan sécurité Backend ====="

                    trivy image \
                      --timeout 30m \
                      --severity HIGH,CRITICAL \
                      --exit-code 1 \
                      172.20.10.5:8082/student-management-backend:latest
                '''
            }
        }

        stage('Push des images vers Nexus') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'nexus-credentials',
                    usernameVariable: 'NEXUS_USER',
                    passwordVariable: 'NEXUS_PASSWORD'
                )]) {
                    sh '''
                        echo "$NEXUS_PASSWORD" | docker login 172.20.10.5:8082 \
                            -u "$NEXUS_USER" \
                            --password-stdin

                        docker push 172.20.10.5:8082/student-management-frontend:latest

                        docker push 172.20.10.5:8082/student-management-backend:latest
                    '''
                }
            }
        }

        stage('Déploiement sur Kubernetes') {
            steps {
                sh '''
                    export KUBECONFIG=/var/lib/jenkins/.kube/config

                    echo "===== Déploiement Kubernetes ====="

                    kubectl apply -f kubernetes/

                    echo "===== Attente du Backend ====="
                    kubectl rollout status deployment/backend -n student-management

                    echo "===== Attente du Frontend ====="
                    kubectl rollout status deployment/frontend -n student-management

                    echo "===== Vérification ====="
                    kubectl get pods -n student-management
                    kubectl get svc -n student-management
                    kubectl get ingress -n student-management
                '''
            }
        }
    }
}