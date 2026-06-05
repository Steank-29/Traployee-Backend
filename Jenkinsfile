pipeline {
    agent any
    
    stages {
        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                sh 'mvnw clean package -DskipTests'
            }
        }
        
        stage('Test') {
            steps {
                echo 'Running tests...'
                sh 'mvnw test'
            }
        }
        
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t traployee-backend .'
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Deploying with Docker Compose...'
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }
    }
}