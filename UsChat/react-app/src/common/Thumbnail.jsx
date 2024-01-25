import { Image } from "react-native"

function Thumbnail({ url, size }) {
	return (
		<Image 
			source={require('../assets/profile.png')}
			style={{ 
				width: size, 
				height: size, 
				borderRadius: size / 2,
				backgroundColor: '#e0e0e0' 
			}}
		/>
	)
}

export default Thumbnail