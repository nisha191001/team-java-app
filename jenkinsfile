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
