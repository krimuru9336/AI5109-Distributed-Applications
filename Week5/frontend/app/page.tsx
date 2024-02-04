import { calculateBMI, getBMIList } from '@/lib/actions'

export default async function Home() {
  const bmiList = await getBMIList();

  return (
    <main className="flex min-h-screen items-center justify-between p-24">
      <form action={calculateBMI} className='flex flex-col'>
        <div className='space-y-1 flex flex-col'>
          <label htmlFor='name'>Name:</label>
          <input className='text-black p-1' type="text" id='name' name='name' />
          <label htmlFor='height'>Height:</label>
          <input className='text-black p-1' type="text" id='height' name='height' />
          <label htmlFor='weight'>Weight:</label>
          <input className='text-black p-1' type="text" id='weight' name='weight' />
        </div>
        <input type='submit' value="Calculate BMI" className='cursor-pointer bg-gray-200 text-black mt-4' />
      </form>
      <div className="relative overflow-x-auto">
        <table className="w-full text-sm text-left rtl:text-right text-gray-500 dark:text-gray-400">
          <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
            <tr>
              <th scope="col" className="px-6 py-3">
                ID
              </th>
              <th scope="col" className="px-6 py-3">
                Name
              </th>
              <th scope="col" className="px-6 py-3">
                Height
              </th>
              <th scope="col" className="px-6 py-3">
                Weight
              </th>
              <th scope="col" className="px-6 py-3">
                BMI
              </th>
            </tr>
          </thead>
          <tbody>
            {bmiList.map((row: any) => <tr key={row.id} className="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
              <th scope="row" className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white">
                {row.id}
              </th>
              <td className="px-6 py-4">
                {row.name}
              </td>
              <td className="px-6 py-4">
                {row.height}
              </td>
              <td className="px-6 py-4">
                {row.weight}
              </td>
              <td className="px-6 py-4">
                {row.bmi}
              </td>
            </tr>)}
          </tbody>
        </table>
      </div>

    </main>
  )
}
