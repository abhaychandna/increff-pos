package com.increff.pos.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.increff.pos.util.IOUtil;

@Controller
public class SampleController {

	
	
	@RequestMapping(value = "/sample/{fileName:.+}", method = RequestMethod.GET)
	public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
		
		response.setContentType("text/csv");
		response.addHeader("Content-disposition:", "attachment; filename=" + fileName);
		String fileClasspath = "/com/increff/pos/" + fileName;
		InputStream is = SampleController.class.getResourceAsStream(fileClasspath);
		
		try {
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtil.closeQuietly(is);
		}

	}

}
