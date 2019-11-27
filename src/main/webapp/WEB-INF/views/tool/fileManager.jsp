<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<title>enianAdmin</title>
	<link rel="stylesheet" type="text/css" href="${path}/res/static/layui/css/layui.css" />
	<link rel="stylesheet" type="text/css" href="${path}/res/static/layui_extends/mouseRightMenu/mouseRightMenu.css" />
	<link rel="stylesheet" type="text/css" href="${path}/res/static/icon/iconfont.css" />
</head>
<body class="layui-layout-body ">
<div id='fileManager' class="fileManagerParent" style="height: 669px;"></div>
<script src="${path}/res/static/layui/layui.js"></script>
<script type="text/javascript">
	layui.config({base: '${path}/res/static/layui/lay/modules/'}).use(['layer','jquery','fileManager','mouseRightMenu'],function(){
		var layer = layui.layer;
		var $ = layui.jquery;
		var fileManager = layui.fileManager;
		var mouseRightMenu = layui.mouseRightMenu;
		$('#checkImgWindow').css('height',$(document).height()-120+'px');
		fileManager.render({
			elem:'#fileManager',
			staticPath:'${path}/res/static/layui_extends/fileManager',  //储存扩展模块所需的css和图片路径
			imgSrc:true,		//为true，图片类型文件会直接按src字段值显示
			filePath: 'F:\\fileManager',   //实际目录
			uploadConfig:{
				url:"/file/fUpload",//上传地址 todo
				before:function(){
					layer.load(); //上传loading
				},
				done:function(result){
					layer.closeAll('loading'); //关闭loading
					if(result.code==1){
						fileManager.reload();
						layer.msg('upload success!');
						// console.log(fileManager.getNowPath());
					}else{
						layer.alert('upload failed!'+result.msg);
					}
				},
				accept:"file",
				exts:"zip|rar|7z|jpg|png|gif|bmp|jpeg|txt"//前端限制后缀
			},
			pageConfig:true, //使用默认分页配置
			pageJump:function(obj,callback){
				var arg={
					'dirId':obj.id,
					'limit':obj.limit,
					'page':obj.page
				};
				//获取分页数据
				ajax("getData.js",arg,function(r,d){
					if(r){
						// console.log(d);
						if(d.code == 1){
							callback(d.fileList,d.count);
						}else{
							layer.alert('加载出现了问题');
						}
					}
				})
			},
			getPathList:function(obj,callback){
				id=(obj.id==0)?'ROOT':obj.id;
				var arg={
					'dirId':obj.id,
					'limit':obj.limit==undefined?50:obj.limit,
					'page':1
				};
				//todo 文件列表返回数据
				$.ajax({
					type:"post",
					url:"/file/getPathList",
					data:arg,
					dataType:"json",
					success:function(d){
						if(d.code == 1){
							callback(d.fileList,d.count);
						}else{
							layer.alert('加载出现了问题');
						}
					},
					async:true
				});
			},
			newDirDone:function(dirObj,name){
				//todo 创建文件
				$.ajax({
					type:"post",
					url:"/file/newDir",
					data:{
						'newDirName':name,
						'dirId':dirObj.id
					},
					dataType:"json",
					success:function(d){
						if(d.code == 1){
							layer.alert('build success!');
							fileManager.reload();
						}else{
							layer.alert('build failed!');
						}
					},
					async:true
				});
			}
		});
		//点击文件
		fileManager.listenClick(function(data){
			/*if(data.type!="/DIR"){
				$.ajax({
					type:"post",
					url:"/file/readFile",
					data:{
						'id': data.id
					},
					dataType:"json",
					success:function(d){
						if(d.code == 0){
							layer.prompt({title: 'content', formType: 2,value:value}, function(text, index){
								layer.close(index);
							});
						}else{
							layer.msg('read failed!');
						}
					},
					async:true
				});
			}*/
		});
		//右键点击文件
		fileManager.listenClickRight(function(data){
			var menu_data=[
				{'data':data,'type':1,'title':'DELETE'},
				{'data':data,'type':2,'title':'RENAME'}
			];
			mouseRightMenu.open(menu_data,false,function(d){
				var checks = fileManager.getMoreCheck();
				var ids = [];
				if(d.type==1){
					layer.confirm('删除后无法恢复，确定要进行删除吗？', function(index){
						if(checks.length!=0){
							for (var i = 0; i < checks.length; i++) {
								ids[i] = checks[i].id;
							}
						}else{
							ids[0]=d.data.id;
						}
						$.ajax({
							type:"post",
							url:"/file/del",
							data:{
								'ids': ids
							},
							dataType:"json",
							success:function(d){
								if(d.code == 1){
									layer.msg('delete success!');
									fileManager.reload();
								}else{
									layer.msg('delete failed!');
								}
							},
							async:true
						});
						layer.close(index);
					});
				}else if(d.type==2){
					layer.prompt({title: 'new filename', formType: 2,value:d.data.name}, function(text, index){
						layer.close(index);
						$.ajax({
							type:"post",
							url:"/file/rename",
							data:{
								'id':d.data.id,
								'newName':text
							},
							dataType:"json",
							success:function(d){
								if(d.code == 1){
									layer.alert('rename success!');
									fileManager.reload();
								}else{
									layer.alert('rename failed!');
								}
							},
							async:true
						});
					});
				}
			});
			return false;
		});
		function ajax(url,data,done){
			$.ajax({
				type:"get",
				url:url,
				data:data,
				dataType:'json',
				success:function(r){
					done(true,r)
				},
				error:function(r){
					done(false,r)
				},
				async:true
			});
		}
	});
</script>
</body>
</html>
