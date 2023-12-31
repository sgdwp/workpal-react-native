/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.react.bridge;

import static org.mockito.Mockito.when;

import com.facebook.soloader.SoLoader;
import java.util.List;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;

/** Tests for {@link BaseJavaModule} and {@link JavaModuleWrapper} */
@PrepareForTest({ReadableNativeArray.class, SoLoader.class})
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "androidx.*", "android.*"})
@RunWith(RobolectricTestRunner.class)
@Ignore("Ignored due to unsupported mocking mechanism with JDK 18")
public class BaseJavaModuleTest {

  @Rule public PowerMockRule rule = new PowerMockRule();

  private List<JavaModuleWrapper.MethodDescriptor> mMethods;
  private JavaModuleWrapper mWrapper;
  private ReadableNativeArray mArguments;

  @Before
  public void setup() {
    ModuleHolder moduleHolder = new ModuleHolder(new MethodsModule());
    mWrapper = new JavaModuleWrapper(null, moduleHolder);
    mMethods = mWrapper.getMethodDescriptors();
    PowerMockito.mockStatic(SoLoader.class);
    mArguments = PowerMockito.mock(ReadableNativeArray.class);
  }

  private int findMethod(String mname, List<JavaModuleWrapper.MethodDescriptor> methods) {
    int posn = -1;
    for (int i = 0; i < methods.size(); i++) {
      JavaModuleWrapper.MethodDescriptor md = methods.get(i);
      if (md.name == mname) {
        posn = i;
        break;
      }
    }
    return posn;
  }

  @Test(expected = NativeArgumentsParseException.class)
  public void testCallMethodWithoutEnoughArgs() throws Exception {
    int methodId = findMethod("regularMethod", mMethods);
    when(mArguments.size()).thenReturn(1);
    mWrapper.invoke(methodId, mArguments);
  }

  @Test
  public void testCallMethodWithEnoughArgs() {
    int methodId = findMethod("regularMethod", mMethods);
    when(mArguments.size()).thenReturn(2);
    mWrapper.invoke(methodId, mArguments);
  }

  @Test
  public void testCallAsyncMethodWithEnoughArgs() {
    // Promise block evaluates to 2 args needing to be passed from JS
    int methodId = findMethod("asyncMethod", mMethods);
    when(mArguments.size()).thenReturn(3);
    mWrapper.invoke(methodId, mArguments);
  }

  @Test
  public void testCallSyncMethod() {
    int methodId = findMethod("syncMethod", mMethods);
    when(mArguments.size()).thenReturn(2);
    mWrapper.invoke(methodId, mArguments);
  }

  private static class MethodsModule extends BaseJavaModule {
    @Override
    public String getName() {
      return "Methods";
    }

    @ReactMethod
    public void regularMethod(String a, int b) {}

    @ReactMethod
    public void asyncMethod(int a, Promise p) {}

    @ReactMethod(isBlockingSynchronousMethod = true)
    public int syncMethod(int a, int b) {
      return a + b;
    }
  }
}
