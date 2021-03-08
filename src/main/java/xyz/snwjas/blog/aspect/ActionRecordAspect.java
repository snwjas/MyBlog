package xyz.snwjas.blog.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import xyz.snwjas.blog.annotation.ActionRecord;
import xyz.snwjas.blog.model.vo.LogVO;
import xyz.snwjas.blog.service.LogService;
import xyz.snwjas.blog.utils.IPUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 操作记录注解切面
 *
 * @author Myles Yang
 */
@Component
@Aspect
@Slf4j
public class ActionRecordAspect {

	/**
	 * 方法返回值符号
	 */
	public static final String METHOD_RETURNING_SIGN = "ret";

	/**
	 * el 表达式 解析器
	 */
	private static final ExpressionParser SPEL_PARSER = new SpelExpressionParser();

	@Autowired
	private LogService logService;


	@Pointcut("@annotation(xyz.snwjas.blog.annotation.ActionRecord)")
	public void pointCut() {
	}

	/**
	 * 方法正常返回的advice
	 *
	 * @param point 方法的连接点
	 * @param ret   函数返回值，void的返回值为null
	 */
	@AfterReturning(value = "pointCut()", returning = METHOD_RETURNING_SIGN, argNames = "point,ret")
	public void afterReturning(JoinPoint point, Object ret) {

		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		ActionRecord annotation = method.getAnnotation(ActionRecord.class);
		// 条件判断是否执行日志记录
		if (StringUtils.hasText(annotation.condition())) {
			try {
				Boolean condition = (Boolean) spell(annotation.condition(),
						new String[]{METHOD_RETURNING_SIGN}, new Object[]{ret});
				if (!(Objects.nonNull(condition) && condition)) {
					return;
				}
			} catch (Exception e) {
				log.error("条件EL表达式解析错误:{}", annotation.condition());
				e.printStackTrace();
				return;
			}
		}

		LogVO logVO = new LogVO();

		// 测试时 @AliasFor 失效，原因未知
		String content = StringUtils.isEmpty(annotation.value())
				? annotation.content()
				: annotation.value();

		if (StringUtils.hasText(content)) {
			try {
				String cont = (String) spell(content,
						signature.getParameterNames(), point.getArgs());
				logVO.setContent(cont);
			} catch (Exception e) {
				log.error("内容EL表达式解析错误:{}", content);
				e.printStackTrace();
				return;
			}
		}

		logVO.setType(annotation.type());

		HttpServletRequest request = IPUtils.getRequest();
		if (Objects.nonNull(request)) {
			String ipAddress = IPUtils.getIpAddress(request);
			logVO.setIpAddress(ipAddress);
		}

		logService.add(logVO);
	}

	/**
	 * el表达式解析
	 *
	 * @param el    表达式
	 * @param names 参数名称数组
	 * @param args  参数数组
	 */
	public Object spell(String el, String[] names, Object[] args) {
		EvaluationContext context = new StandardEvaluationContext();
		for (int i = 0; i < args.length; i++) {
			context.setVariable(names[i], args[i]);
		}
		return SPEL_PARSER.parseExpression(el).getValue(context);
	}

}
