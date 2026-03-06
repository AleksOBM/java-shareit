package ru.practicum.shareit.util.stub;

import lombok.NonNull;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.util.exception.MethodNotImplementedException;

import java.lang.reflect.Method;

@Component
public class MethodNotImplementedBeanPostProcessor implements BeanPostProcessor {

	@Override
	public Object postProcessAfterInitialization(Object bean, @NonNull String beanName)
			throws BeansException {

		Class<?> targetClass = bean.getClass();

		boolean hasAnnotatedMethod = false;
		for (Method method : targetClass.getMethods()) {
			if (method.isAnnotationPresent(MethodNotImplemented.class)) {
				hasAnnotatedMethod = true;
				break;
			}
		}

		if (!hasAnnotatedMethod) {
			return bean;
		}

		ProxyFactory proxyFactory = new ProxyFactory(bean);

		proxyFactory.addAdvice((org.aopalliance.intercept.MethodInterceptor) invocation -> {
			Method method = invocation.getMethod();
			Method implMethod = targetClass.getMethod(
					method.getName(),
					method.getParameterTypes()
			);

			if (implMethod.isAnnotationPresent(MethodNotImplemented.class)) {
				throw new MethodNotImplementedException(
						targetClass.getSimpleName(),
						method.getName()
				);
			}

			return invocation.proceed();
		});

		return proxyFactory.getProxy();
	}
}
