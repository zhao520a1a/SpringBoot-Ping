package com.springboot.ping.mybatis.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springboot.ping.common.util.StringUtil;


/**
 * Velocity模板工具类
 * 创建者： 刘江平
 * 创建时间：2015年7月6日下午1:54:18
 */
public class VelocityTemplateUtils {
	private static final Logger logger = LoggerFactory.getLogger(VelocityTemplateUtils.class);
	private static VelocityEngine ve = null;

	static {
		ve = new VelocityEngine();
		// 可选值："class"--从classpath中读取，"file"--从文件系统中读取
		ve.setProperty("resource.loader", "class");
		// 如果从文件系统中读取模板，那么属性值为org.apache.velocity.runtime.resource.loader.FileResourceLoader
		ve.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		Properties prop = new Properties();
		prop.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
		prop.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
		ve.init(prop);
	}

	/**
	 * 根据模板名称从classpath中获取指定模板文件
	 * @param name
	 *            模板名称。如果模板放在classpath根目录直接写文件名e.g
	 *            VelocityTemplateUtils.getTemplate(example.vm);
	 *            如果模板文件放在classpath中的其他目录VelocityTemplateUtils.getTemplate(
	 *            example/example.vm);
	 * @return
	 */
	public static Template getTemplate(String name) {
		return ve.getTemplate(name);
	}

	public static String merge(String templateString, Map<String, Object> model)
			throws Exception {
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(templateString);
		SimpleNode node = runtimeServices.parse(reader, "tempNode");
		Template template = new Template();
		template.setRuntimeServices(runtimeServices);
		template.setData(node);
		template.initDocument();
		VelocityContext context = new VelocityContext();
		Iterator<Map.Entry<String, Object>> it = model.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Object> ent = it.next();
			context.put(ent.getKey().toString(), ent.getValue());
		}
		StringWriter writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}
	/**
	 * 转换模板生成真实页面
	 * @param context 模板数据
	 * @param templateFile  模板文件
	 * @return
	 */
	public static String mergeTemplate(VelocityContext context,String templateFile) {
		Template template = null;
		StringWriter writer = null;
		try {
			
			template = getTemplate(templateFile);
			writer = new StringWriter();
			if (template != null)
				template.merge(context, writer);
			writer.flush();

		} catch (ResourceNotFoundException e) {
			logger.error(StringUtil.getTrace(e));
		} catch (ParseErrorException e) {
			logger.error(StringUtil.getTrace(e));
		} catch (Exception e) {
			logger.error(StringUtil.getTrace(e));
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				logger.error(StringUtil.getTrace(e));
			}
		}
		return writer.toString();
	}
	/**
	 * 响应到前面页面
	 * @param context 模板数据
	 * @param templateFile 模板文件
	 * @param response 
	 * @throws IOException
	 */
	public static void printlnJsp(VelocityContext context,String templateFile,HttpServletResponse response) throws IOException{
		String jsp = mergeTemplate(context, templateFile);
		response.setContentType("text/html;charset=UTF-8"); 
		PrintWriter out = response.getWriter();
		out.println(jsp);
	}
}
