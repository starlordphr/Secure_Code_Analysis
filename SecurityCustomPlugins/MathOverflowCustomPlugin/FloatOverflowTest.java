
public class FloatOverflowTest {
	
	public FloatOverflowTest()
	{
		
	}
	
	public float addOverflow(float a, float b)
	{
		return a+b;
	}
	
	public float multiplyOverflow(float a, float b)
	{
		return a*b;
	}
	
	public float plusPlusOverflow(float a)
	{
		return a++;
	}
	
	public float plusEqualOverflow(float a)
	{
		a += 1;
		return a;
	}
	
	public float multEqualOverflow(float a)
	{
		a *= 1;
		return a;
	}
	
	public float negationOverflow(float a)
	{
		return -a;
	}
	
	public float okAdd(float a, float b) throws ArithmeticException
	{
		if (a > 0 ? a > Float.MAX_VALUE - b
				: a < Float.MIN_VALUE - b)
		{
				throw new ArithmeticException("Addition will exceed return value");
		}
		
		return a+b;
	}

}
