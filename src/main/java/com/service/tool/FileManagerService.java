package com.service.tool;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Service;

import com.mapper.tool.FileManagerMapper;
import com.model.tool.FileManager;
import com.service.base.CrudService;

/**
 * @功能说明：文件存储信息
 * @作者： nfs
 * @创建日期：2019-10-24
 */
@Service
@Transactional
public class FileManagerService extends CrudService<FileManagerMapper, FileManager> {
	
}