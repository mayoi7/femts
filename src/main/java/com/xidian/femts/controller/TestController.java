package com.xidian.femts.controller;

import com.xidian.femts.exception.ParamException;
import com.xidian.femts.utils.MulFileUtils;
import com.xidian.femts.vo.ResultVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 测试
 *
 * @author LiuHaonan
 * @date 18:00 2020/2/6
 * @email acerola.orion@foxmail.com
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("rte")
    public ResultVO testRuntimeException() {
        throw new RuntimeException();
    }

    @GetMapping("ce")
    public ResultVO testCheckedException() throws Exception {
        throw new Exception();
    }

    @GetMapping("pe")
    public ResultVO testCustomException() throws ParamException {
        throw new ParamException();
    }

    @PostMapping(value = "/upload")
    public ResultVO upload(@RequestParam("file") MultipartFile file,
                           HttpServletRequest request) throws IOException {
//        FileSigner.changeToFile(file);
        return ResultVO.SUCCESS;

        // 表单的 name="name" 属性
//        String name = request.getParameter("name");
//        // 上传的文件名
//        String filename = file.getOriginalFilename();
//        String suffixName = Objects.requireNonNull(filename).substring(filename.lastIndexOf("."));
//
//        // 随机生成
//        filename = UUID.randomUUID() + suffixName;
//        String res = Word2HtmlUtils.Word2007ToHtml(file);
//        System.out.println(res);
//        // TODO: 2020/1/18 改为通过fastfs生成
//        File dest = new File("/file/" + filename);
//
//        try {
//            file.transferTo(dest);
//            return new ResultVO(dest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return new ResultVO("failed");
    }

    @GetMapping("download")
    public ResultVO download(HttpServletResponse response) {
        File file = new File("file/temp/test.docx");
        response.setHeader("Content-Type", "application/octet-stream;charset=utf-8");
        response.setContentType("application/force-download");
        String name = "测试文件.docx";
        try {
            response.addHeader("Content-Disposition", "attachment;filename=" + name + ";filename*=utf-8''"
                    + URLEncoder.encode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            byte[] bytes = MulFileUtils.changeFileToBytes(file);
            OutputStream os = response.getOutputStream();
            os.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
//        return new ResultVO(HttpStatus.INTERNAL_SERVER_ERROR, "发生异常");
    }
}
