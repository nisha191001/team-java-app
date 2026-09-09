pipeline {
    agent any

    parameters {
        choice(
            name: 'IMAGE_TAG',
            choices: ['1.0', '1.1', '1.2'],
            description: 'Docker image version to build and push'
        )
    }

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPOSITORY = 'team-java-app'
        ECR_REGISTRY = ''
        ECR_URI = ''
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Maven Build') {
            steps {
                echo 'Building Java application...'
                sh '''
                    mvn clean package
                '''
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image: ${env.ECR_REPOSITORY}:${params.IMAGE_TAG}"
                sh '''
                    docker build \
                        -t ${ECR_REPOSITORY}:${IMAGE_TAG} \
                        .
                '''
            }
        }

        stage('ECR Login') {
            steps {
                echo 'Authenticating Docker with AWS ECR...'

                script {
                    env.ECR_REGISTRY = sh(
                        script: '''
                            aws ecr describe-repositories \
                                --repository-name ${ECR_REPOSITORY} \
                                --region ${AWS_REGION} \
                                --query 'repositories[0].repositoryUri' \
                                --output text
                        ''',
                        returnStdout: true
                    ).trim()

                    env.ECR_REGISTRY = env.ECR_REGISTRY.split('/')[0]

                    env.ECR_URI = "${env.ECR_REGISTRY}/${env.ECR_REPOSITORY}"

                    sh '''
                        aws ecr get-login-password \
                            --region ${AWS_REGION} \
                        | docker login \
                            --username AWS \
                            --password-stdin ${ECR_REGISTRY}
                    '''
                }
            }
        }

        stage('Docker Tag') {
            steps {
                echo 'Tagging Docker image for ECR...'

                sh '''
                    docker tag \
                        ${ECR_REPOSITORY}:${IMAGE_TAG} \
                        ${ECR_URI}:${IMAGE_TAG}
                '''
            }
        }

        stage('Docker Push') {
            steps {
                echo "Pushing ${ECR_URI}:${params.IMAGE_TAG} to ECR..."

                sh '''
                    docker push \
                        ${ECR_URI}:${IMAGE_TAG}
                '''
            }
        }
    }

    post {
        success {
            echo "SUCCESS: ${ECR_URI}:${params.IMAGE_TAG} pushed to ECR."
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}
