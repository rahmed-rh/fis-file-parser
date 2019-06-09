openshift.withCluster() {
	env.NAMESPACE = openshift.project()
	//env.TOKEN = readFile('/var/run/secrets/kubernetes.io/serviceaccount/token').trim()
}

pipeline {
	environment {
		def timestamp = "${System.currentTimeMillis()}"
	}
	options {
		// set a timeout of 20 minutes for this pipeline
		timeout(time: 20, unit: 'MINUTES')
		// when running Jenkinsfile from SCM using jenkinsfilepath the node implicitly does a checkout
		skipDefaultCheckout()
		// Disallow concurrent executions of the Pipeline
		disableConcurrentBuilds()
	}
	agent any

	stages {
		stage('initialise') {
			steps {
				echo "NAMESPACE is: ${env.NAMESPACE}"
				echo "Build Number is: ${env.BUILD_NUMBER}"
				echo "Job Name is: ${env.JOB_NAME}"
				sh "oc version"
				sh 'printenv'
			}
		}

		stage('Create MySQL') {
			steps {
				script {
					openshift.withCluster() {
						//openshift.verbose() // set logging level for subsequent operations executed (loglevel=8)
						openshift.withProject("${env.NAMESPACE}") {
							if (!openshift.selector('deploymentconfig', 'mysql').exists()) {
								def mysqlDcSelector = openshift.newApp("mysql-ephemeral", "-p MYSQL_USER=user", "-p MYSQL_PASSWORD=passw0rd", "-p MYSQL_ROOT_PASSWORD=passw0rd", "-p MYSQL_DATABASE=sampledb")
								def mysqlDc = mysqlDcSelector.narrow('deploymentconfig')
								timeout(15) {
									mysqlDc.watch {
										echo "Waiting for ${it.name()} to be ready"
										return it.object().status.readyReplicas == 1
									}
								}
							}
						}
					}
				}
			}
		}


		stage('Deploy FIS Application') {
			agent {
				node {
					label 'maven'
				}
			}
			steps {
				script {
					openshift.withCluster() {
						//openshift.verbose() // set logging level for subsequent operations executed (loglevel=8)
						openshift.withProject("${env.NAMESPACE}") {
							// Get some code from a GitHub repository
							git branch: "master", url: "https://github.com/rahmed-rh/fis-file-parser"
							// Run the maven build
							timeout(15) {
								sh "mvn fabric8:deploy -Dfabric8.namespace=${env.NAMESPACE}"
								def appDC = openshift.selector('deploymentconfig', 'fis-file-parser')
								appDC.watch {
									echo "Waiting for ${it.name()} to be ready"
									return it.object().status.readyReplicas == 1
								}
							}

						}
					}
				}
			}
		}

	}
}
