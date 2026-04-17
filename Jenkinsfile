pipeline {
    agent any
    environment {
        APP_PORT = '8082'
        JAR_FILE = 'target/taskmanager-1.0.0.jar'
    }
    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B clean package -DskipTests'
                    } else {
                        bat 'mvn -B clean package -DskipTests'
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn -B test'
                    } else {
                        bat 'mvn -B test'
                    }
                }
            }
        }
        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }
        stage('Deploy') {
            steps {
                script {
                    if (isUnix()) {
                        sh '''#!/bin/bash
if lsof -i:${APP_PORT} -t >/dev/null 2>&1; then
  echo "Stopping existing process on port ${APP_PORT}"
  kill -9 $(lsof -i:${APP_PORT} -t) || true
fi
nohup java -jar ${JAR_FILE} > app.log 2>&1 &
sleep 5
'''                    
                    } else {
                        bat '''@echo off
for /f "tokens=5" %%i in ('netstat -ano ^| findstr /R /C:":%APP_PORT%"') do (
  echo Killing %%i
  taskkill /F /PID %%i || echo Failed to kill %%i
)
start /B java -jar %JAR_FILE% > app.log 2>&1
'''
                    }
                }
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/*.jar, app.log'
        }
    }
}
