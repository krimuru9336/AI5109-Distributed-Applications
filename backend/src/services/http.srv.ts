
/**
 *! Distributed Applications
 ** Name: Iman Emadi
 ** Matriculation number: 1452312
 *? Date: 24/11/2023
 */
export class HTTPService {

    public async fetchData(lat: string, long: string): Promise<Response> {
        // create the API target url with parameters.
        const targetURL = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${long}`;
        return fetch(targetURL, { method: 'GET' }) // make the HTTP request.
    }
}

// example output
//  {
//     latitude: 22.0,
//     longitude: 11.0,
//     generationtime_ms: 0.0020265579223632812,
//     utc_offset_seconds: 0,
//     timezone: "GMT",
//     timezone_abbreviation: "GMT",
//     elevation: 550.0,
// }
