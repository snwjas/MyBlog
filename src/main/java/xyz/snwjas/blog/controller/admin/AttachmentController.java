package xyz.snwjas.blog.controller.admin;


import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import xyz.snwjas.blog.model.PageResult;
import xyz.snwjas.blog.model.R;
import xyz.snwjas.blog.model.entity.AttachmentEntity;
import xyz.snwjas.blog.model.params.AttachmentSearchParam;
import xyz.snwjas.blog.model.params.ListParam;
import xyz.snwjas.blog.model.vo.AttachmentVO;
import xyz.snwjas.blog.service.AttachmentService;
import xyz.snwjas.blog.utils.RUtils;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

/**
 * Attachment Controller
 *
 * @author Myles Yang
 */
@Validated
@RestController("AdminAttachmentController")
@RequestMapping("/api/admin/attachment")
@Api(value = "后台附件控制器", tags = {"后台附件接口"})
public class AttachmentController {

	@Autowired
	private AttachmentService attachmentService;

	@Autowired
	private MultipartProperties multipartProperties;

	@PostMapping("/upload")
	@ApiOperation("上传附件")
	public R upload(@RequestPart("file") MultipartFile file,
	                @RequestParam(value = "team", required = false) String team) {
		// if (file.getSize() > multipartProperties.getMaxFileSize().toBytes()) {
		// 	return RUtils.fail("上传文件过大");
		// }
		AttachmentVO vo = attachmentService.add(file, team);
		if (Objects.isNull(vo)) {
			return RUtils.fail("文件上传出错");
		}
		return RUtils.success("文件上传成功", vo);
	}

	@PostMapping("/list")
	@ApiOperation("条件搜索附件")
	public R search(@RequestBody @Valid AttachmentSearchParam param) {
		IPage<AttachmentEntity> page = attachmentService.pageBy(param);
		PageResult<AttachmentVO> pageResult = attachmentService.covertToPageResult(page);
		return RUtils.success("附件列表信息", pageResult);
	}

	@GetMapping("/list/type")
	@ApiOperation("获取所有文件类型")
	public R listAllMediaTypes() {
		List<String> mediaTypes = attachmentService.listAllMediaTypes();
		return RUtils.success("所有文件类型", mediaTypes);
	}

	@GetMapping("/list/team")
	@ApiOperation("获取所有文件分组")
	public R listAllTeams() {
		List<String> teams = attachmentService.listAllTeams();
		return RUtils.success("所有文件分组", teams);
	}

	@DeleteMapping("/delete/{attachmentId}")
	@ApiOperation("删除附件")
	public R delete(@PathVariable("attachmentId") @Min(1) Integer attachmentId) {
		int i = attachmentService.deleteById(attachmentId);
		return RUtils.commonFailOrNot(i, "删除附件");
	}

	@DeleteMapping("/delete")
	@ApiOperation("批量删除附件")
	public R delete(@RequestBody @Validated ListParam<Integer> attachmentIds) {
		int count = attachmentService.deleteById(attachmentIds);
		return RUtils.success("删除了" + count + "个附件");
	}

	@PostMapping("/update")
	@ApiOperation("修改附件名")
	public R update(@RequestBody @Valid AttachmentVO vo) {
		int i = attachmentService.updateNameById(vo.getId(), vo.getName());
		return RUtils.commonFailOrNot(i, "附件名修改");
	}

	@PostMapping("/update/team")
	@ApiOperation("修改附件分组")
	public R updateTeam(@RequestParam("ids") @Validated ListParam<Integer> ids,
	                    @RequestParam("team") @NotNull String team) {
		int i = attachmentService.updateTeam(ids, team);
		return RUtils.commonFailOrNot(i, String.format("移动到分组[%s]", team));
	}

}
