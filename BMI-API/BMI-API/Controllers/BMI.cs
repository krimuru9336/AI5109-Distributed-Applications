using Microsoft.AspNetCore.Mvc;
using System.Data.SqlClient;
using Microsoft.AspNetCore.Cors;

namespace BMI_API.Controllers;

[ApiController]
[EnableCors("CORS-All")]
[Route("[controller]")]
public class BmiController : ControllerBase
{
    #region Private Members

    private static string connectionString;
    private SqlConnection connection;
    private List<BmiBean> bmis;
    private readonly ILogger<BmiController> _logger;

    #endregion

    #region Constructor

    public BmiController(IConfiguration configuration, ILogger<BmiController> logger)
    {
        connectionString = configuration.GetConnectionString("BMI-DB");


        connection = new SqlConnection(connectionString);
        connection.Open();
        GetBmisFromDb();

        _logger = logger;
    }

    #endregion

    #region Private Methods

    private void GetBmisFromDb()
    {
        bmis = new List<BmiBean>();
        SqlCommand cmd = new SqlCommand("SELECT * FROM bmi.bmi", connection);

        SqlDataReader reader = cmd.ExecuteReader();

        if (reader.HasRows)
        {
            while (reader.Read())
            {
                bmis.Add(
                    new BmiBean()
                    {
                        Name = reader["name"].ToString(),
                        Height = Convert.ToDouble(reader["height"].ToString()),
                        Weight = Convert.ToDouble(reader["weight"].ToString()),
                    });
            }
        }

        reader.Close();
    }

    private bool StoreBmiInDb(BmiBean newValue)
    {
        SqlCommand storeCommand = new SqlCommand("INSERT INTO bmi.bmi (name, height, weight)\nVALUES (N'"
                                                 + newValue.Name + "', " + newValue.Height + "," + newValue.Weight +
                                                 ");\n", connection);
        int rows = storeCommand.ExecuteNonQuery();

        if (rows != 0)
        {
            return true;
        }

        return false;
    }

    #endregion


    # region Http

    [HttpGet(Name = "GetBmiValues")]
    public ActionResult Get()
    {
        GetBmisFromDb();

        return new JsonResult(bmis);
    }


    [HttpPut()]
    public ActionResult<string> Put([FromBody] BmiBean person)
    {
        StoreBmiInDb(person);
        
        return Ok(bmis.ToArray());
    }

    #endregion
}