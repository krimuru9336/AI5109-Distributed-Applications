using System.Text.Json.Serialization;

namespace BMI_API;

public class DuckApiResponse
{
    private string message;
    private string url;

    [JsonPropertyName("message")]
    public string Message
    {
        get => message;
        set => message = value;
    }

    [JsonPropertyName("url")]
    public string Url
    {
        get => url;
        set => url = value;
    }
}