#!/usr/local/bin/python3
# -*- coding: utf-8 -*

import sys
import os
import subprocess
import datetime
import shutil
import time

#获取当前时间
def get_current_time():
	nowTime=datetime.datetime.now().strftime('%Y-%m-%d_%H-%M')
	return nowTime

#执行脚本命令
def execute_cmd(cmd):
	print('execute ' + cmd)
	output = os.popen(cmd)
	result = output.read()
	print(result)
	return result

#从git拉取分支
def checkout():
	srcRoot = '%(srcRoot)s'%(settings)
	branch = '%(branchName)s'%(settings)
	print(srcRoot)
	if not os.path.exists(srcRoot):
		os.makedirs(srcRoot)
	os.chdir(srcRoot)	
	if os.listdir(srcRoot):
		print('目录已经存在，切换到分支')
	else:
		print('分支不存在，拉取分支')
		cmd = 'git clone %(git)s %(srcRoot)s'%(settings)
		execute_cmd(cmd)
	allBranch = execute_cmd('git branch -a')
	if allBranch.find('* ' + branch) < 0:
		if allBranch.find(branch):
			cmd = 'git checkout %s'%(branch)
		else:
			cmd = 'git checkout -b %s origin/%s'%(branch, branch)
		result = execute_cmd(cmd)
		if result == '':
			print("程序退出")
			sys.exit(1)
	print("更新分支...")
	execute_cmd('git pull')


#使用maven打包
def package():
	os.environ['JAVA_HOME'] = '/opt/software/jdk1.8.0_121'
	branch = '%(srcRoot)s'%(settings)
	os.chdir(branch)
	cmd = '/opt/software/apache-maven-3.3.9/bin/mvn clean package -P qa -DSkipTests -Denv=qa -Djava.home=' + os.environ['JAVA_HOME'] + '/jre'
	execute_cmd(cmd)

#备份原来的jar包
def bakup_project():
	source = '%(workDir)s/webapps/%(projectName)s'%settings
	dest = '%(backupDir)s%(projectName)s'%(settings) + '.' + get_current_time()
	if os.path.exists(source):
		print('备份文件：%s到%s'%(source, dest))
		shutil.copyfile(source,dest)
	else:
		print('文件不存在，不需要备份')


#杀死进程
def kill_process_id(name):
	print('杀死进程：%s'%name)
	child = subprocess.Popen(['pgrep', '-f', name], stdout=subprocess.PIPE, shell=False)
	response = child.communicate()[0]
	if response:
     		pid = response.split()[0]
     		cmd = 'kill -9 %s'%pid
     		execute_cmd(cmd)

#杀死tomcat进程
def kill_tomcat_process():
	os.chdir(settings['workDir'])
	execute_cmd('sh %(stop)s'%settings)
	time.sleep(5)

#替换项目中的文件
def replace_package():
	source = '%(srcRoot)s/target/%(targetName)s'%settings
	dest = '%(workDir)s/webapps/%(projectName)s'%(settings)
	print('复制文件：%s到%s'%(source, dest))
	shutil.copyfile(source,dest)


def start_process():
	os.chdir(settings['workDir'])
	execute_cmd('sh %(start)s'%settings)
	print("启动成功")
	

if __name__ == '__main__':
	branchName = 'branch-20180809-chanpinqianyi'
	if len(sys.argv) <= 1:
		print("未获取分支名称，将使用默认分支", branchName)
	else:
		branchName = sys.argv[1]
	settings = {
		'git': 'git@git.iqdnet.cn:bigdata/qddata.git', #svn地址
		'srcRoot': '/opt/web/project/qddata/', #源代码存放位置
		'branchName': branchName, #分支名称
		'workDir': '/opt/web/workDir/qddata', #生产环境项目的根路径
		'backupDir':'/opt/web/workDir/qddata/backup/',  #备份地址
		'targetName':'compass.war',
		'projectName':'compass.war',   #webapps下的名称
		'start':'start.sh',
		'stop':'shutdown.sh',
	}
	checkout()
	package()
	bakup_project()
	#kill_process_id(settings['targetName'])
	kill_tomcat_process()
	kill_tomcat_process()
	replace_package()
	start_process()
