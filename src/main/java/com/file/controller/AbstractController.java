package com.file.controller;

import com.file.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * Controller公共组件
 * 
 * @author jason
 *
 */
abstract class AbstractController {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	/** 常量帮助类 */
	@Resource
	protected Constant constant;

}
