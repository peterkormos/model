package datatype;

import org.junit.Assert;
import org.junit.Test;

public class TestAgeGroup
{
  @Test
  public void testGet()
  {
	Assert.assertEquals(AgeGroup.CHILD, AgeGroup.get(1));
	Assert.assertEquals(AgeGroup.CHILD, AgeGroup.get(5));
	Assert.assertEquals(AgeGroup.CHILD, AgeGroup.get(12));
	Assert.assertEquals(AgeGroup.YOUNGSTER, AgeGroup.get(13));
	Assert.assertEquals(AgeGroup.YOUNGSTER, AgeGroup.get(14));
	Assert.assertEquals(AgeGroup.YOUNGSTER, AgeGroup.get(15));
	Assert.assertEquals(AgeGroup.JUNIOR, AgeGroup.get(16));
	Assert.assertEquals(AgeGroup.JUNIOR, AgeGroup.get(17));
	Assert.assertEquals(AgeGroup.JUNIOR, AgeGroup.get(18));
	Assert.assertEquals(AgeGroup.ADULT, AgeGroup.get(19));
	Assert.assertEquals(AgeGroup.ADULT, AgeGroup.get(21));
	Assert.assertEquals(AgeGroup.ADULT, AgeGroup.get(99));
  }

  @Test
  public void testisIn()
  {
	Assert.assertTrue(AgeGroup.ADULT.contains(AgeGroup.ADULT));

	Assert.assertTrue(AgeGroup.ALL.contains(AgeGroup.ADULT));
	Assert.assertTrue(AgeGroup.ALL.contains(AgeGroup.CHILD));
	Assert.assertTrue(AgeGroup.ALL.contains(AgeGroup.YOUNGSTER));
	Assert.assertTrue(AgeGroup.ALL.contains(AgeGroup.JUNIOR));

	Assert.assertFalse(AgeGroup.ADULT.contains(AgeGroup.JUNIOR));
  }
}
