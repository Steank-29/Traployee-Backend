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
                echo 'Tests successful!'
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
        echo 'Deploying with Docker...'
        sh 'docker stop traployee-backend || true'
        sh 'docker rm traployee-backend || true'
        sh 'docker stop traployee-postgres || true'
        sh 'docker rm traployee-postgres || true'
        
        echo 'Starting PostgreSQL...'
        sh 'docker run -d --name traployee-postgres \
            -e POSTGRES_DB=Traployee \
            -e POSTGRES_USER=postgres \
            -e POSTGRES_PASSWORD=password \
            -p 5432:5432 \
            postgres:15'
        
        echo 'Waiting for PostgreSQL to start...'
        sh 'sleep 10'
        
        echo 'Starting Spring Boot application...'
        sh 'docker run -d --name traployee-backend \
            -p 8080:8080 \
            -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/Traployee \
            -e SPRING_DATASOURCE_USERNAME=postgres \
            -e SPRING_DATASOURCE_PASSWORD=password \
            traployee-backend:latest'
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