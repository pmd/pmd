package net.sourceforge.pmd.lang.java.metrics.impl;

import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;

/**
 * @author Denis Borovikov
 */
public class CognitiveComplexityTestRule extends AbstractMetricTestRule{

  @Override
  protected JavaClassMetricKey getClassKey() {
    return null;
  }

  @Override
  protected JavaOperationMetricKey getOpKey() {
    return JavaOperationMetricKey.COGNITIVE_COMPLEXITY;
  }
}
