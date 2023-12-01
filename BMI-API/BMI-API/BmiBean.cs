namespace BMI_API;

public class BmiBean
{
    public string Name { get; set; }
    public double Height { get; set; }
    public double Weight { get; set; }

    public double BMI
    {
        get => Weight / (Height * Height);
    }
}