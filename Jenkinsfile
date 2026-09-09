pipeline {
    agent any

    parameters {
        choice(
            name: 'IMAGE_TAG',
            choices: ['1.0', '1.1', '1.2'],
            description: 'Docker image tag'
        )
    }

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPOSITORY = 'team-java-app'
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
                sh 'mvn clean package'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker image: team-java-app:${IMAGE_TAG}"
                sh 'docker build -t team-java-app:${IMAGE_TAG} .'
            }
        }

        stage('ECR Login') {
            steps {
                echo 'Authenticating Docker with AWS ECR...'

                script {
                    env.ECR_URI = sh(
                        script: '''
                            aws ecr describe-repositories \
                            --repository-name "$ECR_REPOSITORY" \
                            --region "$AWS_REGION" \
                            --query "repositories[0].repositoryUri" \
                            --output text
                        ''',
                        returnStdout: true
                    ).trim()

                    if (!env.ECR_URI || env.ECR_URI == 'None') {
                        error('ECR repository URI could not be determined.')
                    }

                    echo "ECR Repository URI: ${env.ECR_URI}"

                    sh '''
                        aws ecr get-login-password --region "$AWS_REGION" |
                        docker login --username AWS --password-stdin "$ECR_URI"
                    '''
                }
            }
        }

        stage('Docker Tag') {
            steps {
                echo 'Tagging Docker image...'

                sh '''
                    docker tag \
                    team-java-app:${IMAGE_TAG} \
                    ${ECR_URI}:${IMAGE_TAG}
                '''
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Docker image to ECR...'

                sh '''
                    docker push ${ECR_URI}:${IMAGE_TAG}
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully.'
        }

        failure {
            echo 'Pipeline failed.'
        }
    }
}
