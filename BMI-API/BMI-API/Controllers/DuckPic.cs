using Microsoft.AspNetCore.Mvc;
using System.Data.SqlClient;
using System.IO.Enumeration;
using System.Text.Json;
using Microsoft.AspNetCore.Cors;
using RestSharp;

namespace BMI_API.Controllers;

[EnableCors("CORS-All")]
[ApiController]
[Route("[controller]")]
public class DuckPicController : ControllerBase
{
    #region Private Members

    private readonly ILogger<DuckPicController> _logger;

    #endregion

    #region Constructor

    public DuckPicController(ILogger<DuckPicController> logger)
    {
        _logger = logger;
    }

    #endregion

    #region Http

    [EnableCors]
    [HttpGet(Name = "GetDuckPicUrl")]
    public ActionResult Get()
    {
        RestClientOptions options = new RestClientOptions();
        options.BaseUrl = new Uri("https://random-d.uk/api/random");
        RestSharp.RestClient client = new RestClient(options);
        RestRequest req = new RestRequest();
        var response = client.Get(req);

        if (response.IsSuccessful)
        {
            DuckApiResponse duckResponse = JsonSerializer.Deserialize<DuckApiResponse>(response.Content);
            return new JsonResult(duckResponse);
        }
        else
        {
            return new NotFoundResult();
        }
    }

    #endregion
}