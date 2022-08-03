/*
 * Copyright (C) 2019 DV Bern AG, Switzerland
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

@Library('dvbern-ci') _

properties([
		[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', numToKeepStr: '10']],
		parameters([
				booleanParam(defaultValue: false, description: 'Do you want to perform a Release?', name:
						'performRelease'),
				string(defaultValue: '', description: 'This release version', name: 'releaseversion', trim:
						true),
				string(defaultValue: '', description: 'The next release version', name: 'nextreleaseversion',
						trim: true)
		])
])

def jdk = "OpenJDK_11.0.4"
// comma separated list of email addresses of all team members (for notification)
def recipients = "fabio.heer@dvbern.ch"

def masterBranchName = "master"
def developBranchName = "develop"
def featureBranchPrefix = "feature"
def releaseBranchPrefix = "release"
def hotfixBranchPrefix = "hotfix"

if (params.performRelease) {
	// see https://issues.jenkins-ci.org/browse/JENKINS-53512
	def releaseVersion = params.releaseversion
	def nextReleaseVersion = params.nextreleaseversion

	dvbJGitFlowRelease {
		releaseversion = releaseVersion
		nextreleaseversion = nextReleaseVersion
		emailRecipients = recipients
		jdkVersion = jdk
		credentialsId = 'jenkins-github-token'
	}
} else {
	node('docker') {
		stage('Checkout') {
			checkout([
					$class           : 'GitSCM',
					branches         : scm.branches,
					extensions       : scm.extensions + [[$class: 'LocalBranch', localBranch: '']],
					userRemoteConfigs: scm.userRemoteConfigs
			])
		}

		String branch = env.BRANCH_NAME.toString()
		currentBuild.displayName = "${branch}-${dvbMaven.pomVersion()}-${env.BUILD_NUMBER}"

		def handleFailures = {error ->
			if (branch.startsWith(featureBranchPrefix)) {
				// feature branche failures should only notify the feature owner
				step([
						$class                  : 'Mailer',
						notifyEveryUnstableBuild: true,
						recipients              : emailextrecipients([[$class: 'RequesterRecipientProvider']]),
						sendToIndividuals       : true])

			} else {
				dvbErrorHandling.sendMail(recipients, currentBuild, error)
			}
		}

		stage('Maven build') {

			// in develop and master branches attempt to deploy the artifacts, otherwise only run to the verify
			// phase.
			def branchSpecificGoal = {
				def developGoal = "deploy docker:build docker:push"
				if (branch.startsWith(developBranchName)) {
					return developGoal
				}

				if (branch.startsWith(masterBranchName)) {
					return developGoal + " -Ddocker.tag.latest=latest"
				}

				return "verify"
			}

			try {
				withMaven(jdk: jdk) {
					dvbUtil.genericSh(
							'./mvnw -U -B -Pdvbern.oss -Dmaven.test.failure.ignore=true clean ' + branchSpecificGoal()
					)
				}
				if (currentBuild.result == "UNSTABLE") {
					handleFailures("build is unstable")
				}
			} catch (Exception e) {
				currentBuild.result = "FAILURE"
				handleFailures(e)
			}
		}

        stage('Analysis') {
			recordIssues enabledForFailure: true, qualityGates: [[threshold: 1, type: 'TOTAL', unstable: false]],
					tools: [
							checkStyle(),
							pmdParser(),
							spotBugs(pattern: '**/target/spotbugs/spotbugsXml.xml', useRankAsPriority: true)
					]
		}

		if ((branch.startsWith(masterBranchName) || branch.startsWith(developBranchName)) && currentBuild.result == "SUCCESS") {
			stage('Deploy') {
				def deploymentConfig = branch.startsWith(masterBranchName) ?
						'kibon-exchange-uat' :
						'kibon-exchange-dev'

				sshPublisher(publishers: [sshPublisherDesc(
						configName: deploymentConfig,
						transfers: [sshTransfer(
								cleanRemote: false,
								excludes: '',
								execCommand: '/opt/kibon-exchange-service/restart.sh',
								execTimeout: 120000,
								flatten: false,
								makeEmptyDirs: false,
								noDefaultExcludes: false,
								patternSeparator: '[, ]+',
								remoteDirectory: '',
								remoteDirectorySDF:
										false,
								removePrefix: '',
								sourceFiles: '')],
						usePromotionTimestamp: false,
						useWorkspaceInPromotion: false,
						verbose: false)])
			}
		}
	}
}
