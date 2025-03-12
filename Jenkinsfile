pipeline {
    agent any
    environment {
        MODULE = ""  // Jenkins Job 이름을 자동으로 MODULE로 사용
        BRANCH = 'develop'
        DEPLOY_STAGE = 'dev'
    }
    parameters {
        choice choices: ['develop', 'stage', 'main'], name: 'GIT_REF_MANUAL'
    }
    stages {
        stage('Initialize BRANCH & DEPLOY_STAGE') {
            steps {
               script {
                   env.MODULE = JOB_NAME

                    // GIT_REF 값을 기반으로 DEPLOY_STAGE 설정
                    def gitRefMap = [
                        'develop': 'dev',
                        'stage'  : 'stage',
                        'main'   : 'production'
                    ]
                    try {
                        def refs = "$GIT_REF".split('/')
                        BRANCH = refs[refs.size()-1]
                        DEPLOY_STAGE = gitRefMap.get(BRANCH, null)

                    } catch(Exception e) {
                        BRANCH = params.GIT_REF_MANUAL
                        DEPLOY_STAGE = gitRefMap.get(params.GIT_REF_MANUAL, null)
                    }

                    if(!DEPLOY_STAGE) {
                        error "Invalid GIT_REF: ${GIT_REF}"
                    }

                    echo "DEPLOY_STAGE initialized: ${DEPLOY_STAGE}"
                }
            }
        }
        stage('Checkout Code') {
            steps {
                script {
                    git credentialsId: "${GIT_ACCOUNT}", url: "${GIT_REPO}", branch: "${BRANCH}"
                    echo "Code checked out: ${BRANCH}"
                }
            }
        }

        stage('Build & Package Modules') {
            steps {
                script {
                    sh "./gradlew ${MODULE}"
                    echo "Running Gradle Build Completed"
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {

                    withCredentials([string(credentialsId: "aws-${DEPLOY_STAGE}-account", variable: 'AWS_ACCOUNT_ID')]) {
                        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                        def imageTag = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${PROJECT_NAME}/${module}"
                        def image = docker.build("${imageTag}")

                        docker.withRegistry("https://${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com", "ecr:${AWS_REGION}:${AWS_CREDENTIAL_ID}") {
                            image.push("${BUILD_NUMBER}")
                            image.push("latest")
                        }
                        echo "Docker image pushed to ECR: ${imageTag}:${BUILD_NUMBER}"
                    }
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                script {
                    def selectedModules = params.MODULE == 'all' ? MODULES : [params.MODULE]
                    withCredentials([sshUserPrivateKey(credentialsId: "ec2-user", keyFileVariable: 'SSH_KEY')]) {
                        def remote = [
                             host: "${EC2_HOST}",
                             user: 'ec2-user',
                             identityFile: SSH_KEY,
                             allowAnyHosts: true
                        ]
                        def modulePorts = [
                            "user-api": "8082",
                            "apt-batch": "8086",
                            "city-batch": "8085"
                        ]
                        def imageTag = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${PROJECT_NAME}/${module}:latest"
                        def modulePort = modulePorts[env.MODULE] ?: "8080" // 기본값 8080 (예외처리)

                        try {

                            echo "Log in to Docker in EC2"

                            sshCommand remote: remote, command: "docker login -u AWS -p \$(aws ecr get-login-password --region ${AWS_REGION}) ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"

                            echo "Stopping and removing existing container"
                            sshCommand remote: remote, command: "docker stop ${module} || true"
                            sshCommand remote: remote, command: "docker rm ${module} || true"

                            echo "Removing old Docker image"
                            sshCommand remote: remote, command: "docker rmi ${imageTag} || true"

                            echo "Pulling latest Docker image"
                            sshCommand remote: remote, command: "docker pull ${imageTag}"

                            echo "Starting new container"
                            sshCommand remote: remote, command: "docker run -d --rm --name ${module} -p ${modulePort}:${modulePort} ${imageTag}"
                        } catch (e) {
                            echo "Failed to deploy ${module} on EC2"
                        }
                    }
                }
            }
        }
    }
}


