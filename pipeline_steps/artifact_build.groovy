
def get_rpc_repo_creds(){
  return [
    string(
      credentialsId: "RPC_REPO_IP",
      variable: "REPO_HOST"
    ),
    string(
      credentialsId: "RPC_REPO_SSH_USERNAME_TEXT",
      variable: "REPO_USER"
    ),
    file(
      credentialsId: "RPC_REPO_SSH_USER_PRIVATE_KEY_FILE",
      variable: "REPO_USER_KEY"
    ),
    file(
      credentialsId: "RPC_REPO_SSH_HOST_PUBLIC_KEY_FILE",
      variable: "REPO_HOST_PUBKEY"
    ),
    file(
      credentialsId: "RPC_REPO_GPG_SECRET_KEY_FILE",
      variable: "GPG_PRIVATE"
    ),
    file(
      credentialsId: "RPC_REPO_GPG_PUBLIC_KEY_FILE",
      variable: "GPG_PUBLIC"
    )
  ]
}

def apt() {
  common.use_node('ArtifactBuilder2') {
    common.withRequestedCredentials("rpc_repo") {
      common.prepareRpcGit("auto", env.WORKSPACE)
      dir("${env.WORKSPACE}/rpc-openstack") {
        sh """#!/bin/bash
        scripts/artifacts-building/apt/build-apt-artifacts.sh
        """
      } // dir
    } // withCredentials
  } // use_node
}

def git(String image) {
  common.use_node(image) {
    try {
      common.withRequestedCredentials("rpc_repo") {
        common.prepareRpcGit()
        dir("/opt/rpc-openstack/") {
          sh """#!/bin/bash
          scripts/artifacts-building/git/build-git-artifacts.sh
          """
        } // dir
      } // withCredentials
    } catch (e) {
      print(e)
      throw e
    } finally {
      common.archive_artifacts()
    }
  } // use_node
}

def python(String image) {
  common.use_node(image) {
    try {
      common.withRequestedCredentials("rpc_repo") {
        common.prepareRpcGit()
        dir("/opt/rpc-openstack/") {
          sh """#!/bin/bash
          scripts/artifacts-building/python/build-python-artifacts.sh
          """
        } // dir
      } // withCredentials
    } catch (e) {
      print(e)
      throw e
    } finally {
      common.archive_artifacts()
    }
  } // use_node
}

return this
