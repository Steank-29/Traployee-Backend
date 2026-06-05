pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                echo 'Building Spring Boot application...'
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }
        
        stage('Start Test Database') {
            steps {
                echo 'Starting PostgreSQL for tests...'
                sh 'docker stop traployee-test-postgres || true'
                sh 'docker rm traployee-test-postgres || true'
                // IMPORTANT: Use "Traployee" with capital T
                sh 'docker run -d --name traployee-test-postgres \
                    -e POSTGRES_DB=Traployee \
                    -e POSTGRES_USER=postgres \
                    -e POSTGRES_PASSWORD=password \
                    -p 5432:5432 \
                    postgres:15'
                echo 'Waiting for PostgreSQL to start...'
                sh 'sleep 10'
            }
        }
        
        stage('Run Tests') {
            steps {
                echo 'Running tests with test database...'
                sh './mvnw test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        
        stage('Stop Test Database') {
            steps {
                echo 'Stopping test database...'
                sh 'docker stop traployee-test-postgres || true'
                sh 'docker rm traployee-test-postgres || true'
            }
        }
        
        stage('Docker Build') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t traployee-backend:latest .'
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Deploying with Docker Compose...'
                sh 'docker-compose down || true'
                sh 'docker-compose up -d'
            }
        }
        
        stage('Verify') {
            steps {
                echo 'Waiting for application to start...'
                sh 'sleep 15'
                sh 'docker ps'
                echo '✅ Application deployed successfully!'
            }
        }
    }
    
    post {
        success {
            echo '🎉 Pipeline completed!'
            echo 'App: http://localhost:8080'
            echo 'Jenkins: http://localhost:8081'
        }
        failure {
            echo '💥 Pipeline failed. Check logs above.'
        }
    }
}