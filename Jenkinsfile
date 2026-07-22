pipeline {

    agent any

    environment {
        NEXUS_REGISTRY = '192.168.93.142:8081'

        FRONTEND_IMAGE = "${NEXUS_REGISTRY}/frontend"
        BACKEND_IMAGE  = "${NEXUS_REGISTRY}/backend"

        DOCKER_CREDENTIALS = credentials('nexus-credentials')
    }

    stages {

        stage('Checkout') {
            steps {
                git credentialsId: 'github-credentials',
                    url: 'https://github.com/MedAzizBenSaid/Stage_DevSecOps.git',
                    branch: 'main'
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Build Docker Images') {
            steps {
                sh 'docker build -t ${FRONTEND_IMAGE}:latest ./frontend'
                sh 'docker build -t ${BACKEND_IMAGE}:latest ./backend'
            }
        }

        stage('Push Images to Nexus') {
            steps {
                sh '''
                    echo "$DOCKER_CREDENTIALS_PSW" | docker login ${NEXUS_REGISTRY} \
                    -u "$DOCKER_CREDENTIALS_USR" \
                    --password-stdin

                    docker push ${FRONTEND_IMAGE}:latest
                    docker push ${BACKEND_IMAGE}:latest
                '''
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    kubectl --kubeconfig=/var/lib/jenkins/.kube/config \
                    set image deployment/frontend \
                    frontend=${FRONTEND_IMAGE}:latest \
                    -n student-management

                    kubectl --kubeconfig=/var/lib/jenkins/.kube/config \
                    set image deployment/backend \
                    backend=${BACKEND_IMAGE}:latest \
                    -n student-management
                '''
            }
        }

        stage('Verify Deployment') {
            steps {
                sh '''
                    kubectl --kubeconfig=/var/lib/jenkins/.kube/config \
                    rollout status deployment/frontend \
                    -n student-management

                    kubectl --kubeconfig=/var/lib/jenkins/.kube/config \
                    rollout status deployment/backend \
                    -n student-management
                '''
            }
        }
    }
}