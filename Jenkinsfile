pipeline {
  agent any
  environment {
    REGISTRY = 'docker.io/bachir1915'
    TAG = "${env.BUILD_NUMBER}"
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Tests') {
      parallel {
        stage('Backend (Maven)') {
          steps { sh 'mvn -B test' }
          post { always { junit 'target/surefire-reports/*.xml' } }
        }
        stage('ML (pytest)') {
          steps {
            dir('ml-service') {
              sh 'python3 -m venv .venv && . .venv/bin/activate && pip install -q -r requirements.txt && python train_model.py && pytest -q'
            }
          }
        }
      }
    }
    stage('Build images') {
      steps {
        sh '''
          docker build -t $REGISTRY/bergerie-backend:$TAG .
          docker build -t $REGISTRY/bergerie-ml:$TAG ml-service
          docker build -t $REGISTRY/bergerie-frontend:$TAG frontend
        '''
      }
    }
    stage('Push') {
      when { branch 'main' }
      steps {
        withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'U', passwordVariable: 'P')]) {
          sh '''
            echo "$P" | docker login -u "$U" --password-stdin
            for i in backend ml frontend; do docker push $REGISTRY/bergerie-$i:$TAG; done
          '''
        }
      }
    }
    stage('Deploy') {
      when { branch 'main' }
      steps { sh 'docker compose up -d --build' }
    }
  }
}
