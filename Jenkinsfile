stage('ECR Login') {
    steps {
        echo 'Authenticating Docker with AWS ECR...'
        script {
            ECR_URI = sh(
                script: '''
                    aws ecr describe-repositories \
                    --repository-name team-java-app \
                    --region us-east-1 \
                    --query "repositories[0].repositoryUri" \
                    --output text
                ''',
                returnStdout: true
            ).trim()

            echo "ECR Repository URI: ${ECR_URI}"

            sh """
                aws ecr get-login-password --region us-east-1 | \
                docker login --username AWS --password-stdin ${ECR_URI}
            """
        }
    }
}

stage('Docker Tag') {
    steps {
        echo "Tagging Docker image..."
        script {
            sh """
                docker tag team-java-app:${IMAGE_TAG} ${ECR_URI}:${IMAGE_TAG}
            """
        }
    }
}

stage('Docker Push') {
    steps {
        echo "Pushing Docker image to ECR..."
        script {
            sh """
                docker push ${ECR_URI}:${IMAGE_TAG}
            """
        }
    }
}
