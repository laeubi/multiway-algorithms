package com.github.waikatodatamining.multiway.data;

import junit.framework.TestCase;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 * Test cases for the static {@link MathUtils} methods.
 *
 * @author Steven Lang
 */
public class MathUtilsTest extends TestCase {

  /**
   * Example from:
   * <a href="https://www.wolframalpha.com/input/?i=invert+matrix+((1,2),(3,4),(5,6))"/>
   */
  public void testPseudoInvert() {
    INDArray X = Nd4j.create(new double[][]{{1, 2}, {3, 4}, {5, 6}});
    INDArray expected = Nd4j.create(new double[][]{{-16, -4, 8}, {13, 4, -5}}).mul(1 / 12d);
    INDArray actual = MathUtils.pseudoInvert2(X, false);
    assertEquals(expected, actual);

    final INDArray identity3x3 = Nd4j.create(new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}});
    final INDArray identity2x2 = Nd4j.create(new double[][]{{1, 0}, {0, 1}});
    final double precision = 1e-5;

    System.out.println("X = \n" + X);

    // right inverse
    final INDArray rightInverseCheck = X.mmul(actual);
    System.out.println("\nX * X^+ = \n" + rightInverseCheck);

    // right inverse must not hold since X rows are not linear independent (x_3 + x_1 = 2*x_2)

    assertFalse(rightInverseCheck.equalsWithEps(identity3x3, precision));

    // left inverse must hold since X columns are linear independent
    final INDArray leftInverseCheck = actual.mmul(X);
    System.out.println("\nX^+ * X = \n" + leftInverseCheck);
    assertTrue(leftInverseCheck.equalsWithEps(identity2x2, precision));

    // general condition
    final INDArray generalCond = X.mmul(actual).mmul(X);
    System.out.println("\nX * X^+ * X = \n" + generalCond);
    assertTrue(X.equalsWithEps(generalCond, precision));

  }

  /**
   * Testing the wikipedia example from here:
   * <a href="https://en.wikipedia.org/wiki/Kronecker_product#Khatri%E2%80%93Rao_product"/>
   */
  public void testKhatriRaoProductColumnWise() {
    INDArray C = Nd4j.create(new double[][]{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}});
    INDArray D = C.transpose();
    System.out.println("C = " + C);
    System.out.println("D = " + D);

    INDArray expected = Nd4j.create(new double[][]{
      {1, 8, 21},
      {2, 10, 24},
      {3, 12, 27},
      {4, 20, 42},
      {8, 25, 48},
      {12, 30, 54},
      {7, 32, 63},
      {14, 40, 72},
      {21, 48, 81},
    });

    INDArray actual = MathUtils.khatriRaoProductColumnWise(C, D);
    assertEquals(expected, actual);


    /*
    Second example from R:
    ---
    x <- matrix(seq(20),4,5,byrow=TRUE)
    y <- matrix(c(21:40),4,5,byrow=TRUE)
    res <- KhatriRao(x,y)
    ---
    */
    final INDArray arange = Nd4j.arange(1, 21).reshape(4, 5);
    final INDArray arange2 = Nd4j.arange(21, 41).reshape(4, 5);
    final INDArray prod = MathUtils.khatriRaoProductColumnWise(arange, arange2);

    INDArray res = Nd4j.create(new double[][]{
      {21, 44, 69, 96, 125},
      {26, 54, 84, 116, 150},
      {31, 64, 99, 136, 175},
      {36, 74, 114, 156, 200},
      {126, 154, 184, 216, 250},
      {156, 189, 224, 261, 300},
      {186, 224, 264, 306, 350},
      {216, 259, 304, 351, 400},
      {231, 264, 299, 336, 375},
      {286, 324, 364, 406, 450},
      {341, 384, 429, 476, 525},
      {396, 444, 494, 546, 600},
      {336, 374, 414, 456, 500},
      {416, 459, 504, 551, 600},
      {496, 544, 594, 646, 700},
      {576, 629, 684, 741, 800}
    });

    assertEquals(prod, res);
  }
}