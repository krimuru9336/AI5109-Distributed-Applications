import Box from '@mui/material/Box';
import Grid from '@mui/material/Grid';
import Paper from '@mui/material/Paper';
import { styled } from '@mui/material/styles';
import { Link } from 'react-router-dom';

const Item = styled(Paper)(({ theme }) => ({
  ...theme.typography.body2,
  textAlign: 'center',
  color: theme.palette.text.secondary,
  height: 60,
  lineHeight: '60px',
  width: 100
}));


function Home() {

  const exercises = [
    {
      name: 'Ex 1',
      route: '/ex1'
    }, {
      name: 'Ex 2',
      route: '/ex2'
    }]

  return (
    <Grid container spacing={2}>
      <Grid item xs={6}>
        <Box
          sx={{
            p: 2,
            borderRadius: 2,
            display: 'grid',
            gridTemplateColumns: { md: '1fr 1fr' },
            gap: 2,
          }}
        >
          {
            exercises.map((item) => {
              return <Link to={item.route}>
                <Item elevation={12} on>
                  {item.name}
                </Item>
              </Link>
            })
          }
        </Box>
      </Grid>
    </Grid>
  )
}

export default Home
