pipeline {
  agent any
  tools {
    maven 'maven3'
    jdk 'jdk17'
  }
  stages {
    stage('Checkout') {
      steps {
        git branch: 'main', credentialsId: 'githup-cred', url: 'https://github.com/bachir1915/Bergerie_Manager.git'
      }
    }
    stage('Compile') {
      steps { sh 'mvn -B clean compile' }
    }
    stage('Test') {
      steps { sh 'mvn -B test' }
      post { always { junit 'target/surefire-reports/*.xml' } }
    }
    stage('Package') {
      steps { sh 'mvn -B package -DskipTests' }
    }
    // Le conteneur Jenkins du projet monte /var/run/docker.sock mais n'a pas la CLI docker : étape ignorée si absente.
    stage('Docker build') {
      when { expression { sh(script: 'command -v docker', returnStatus: true) == 0 } }
      steps {
        sh '''
          docker build -t bergerie-backend:${BUILD_NUMBER} .
          docker build -t bergerie-ml:${BUILD_NUMBER} ml-service
          docker build -t bergerie-frontend:${BUILD_NUMBER} frontend
        '''
      }
    }
  }
  post { always { cleanWs() } }
}
