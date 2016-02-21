package org.buru.sha.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;

import org.buru.sardine.annotation.Annotations;
import org.buru.sardine.log.Logs;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * introduction
 * 
 * @author bruce_sha (lbs.sha@gmail.com)
 * @version 1.0 2013-3-23
 */
public class AnnotationsTest {

	@Ignore
	@Test
	public void test_annotations() {
		Collection<Annotation> as = Annotations.annotations(Model.class);
		Logs.p("buru  %s", as.size());
		Assert.assertTrue(!as.isEmpty());
	}

	@Ignore
	@Test
	public void test_annotation() {
		AT1 at1 = Annotations.annotation(Model.class, AT1.class);
		Assert.assertNotNull(at1);

		AT3 at3 = Annotations.annotation(Model.class, AT3.class);
		Assert.assertNotNull(at3);

	}

	@Test
	public void test_annotations_method() {

		Method[] ms = AbstractModel.class.getDeclaredMethods();

		for (Method m : ms) {
			Collection<Annotation> as = Annotations.annotations(m);
			Logs.p("buru  %s", as.size());
		}
	}

	@Test
	public void test_annotation_method() {

		Method[] ms = AbstractModel.class.getDeclaredMethods();

		for (Method m : ms) {
			AT1 at1 = Annotations.annotation(m, AT1.class);
			Assert.assertNotNull(at1);
			AT3 at3 = Annotations.annotation(m, AT3.class);
			Assert.assertNotNull(at3);
		}
	}

	class Model extends AbstractModel {
		@Override
		public void call() {
		}
	}

	// @AT1
	abstract class AbstractModel implements IModel {
		@AT1
		@Override
		public void call() {
		}
	}

	// @AT3
	interface IModel {
		@AT3
		void call();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Documented
	public @interface AT1 {
	}

	@Retention(RetentionPolicy.CLASS)
	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Documented
	public @interface AT2 {
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.TYPE, ElementType.METHOD })
	@Documented
	public @interface AT3 {
	}
}
