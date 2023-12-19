"use client";
// Rishabh Goswami
// matriculation number: 1455991
//28 October 2023
import React from "react";
import { useRouter } from "next/navigation";

export default function TableEdit() {
	const [submitting, setsubmitting] = React.useState(false);
	const router = useRouter();

	const clearInputFields = () => {
		const nameInput = document.getElementById("name") as HTMLInputElement;
		const mobileInput = document.getElementById("mobile") as HTMLInputElement;
		if (nameInput) {
			nameInput.value = "";
		}
		if (mobileInput) {
			mobileInput.value = "";
		}
	};
	const submitData = async (e: any) => {
		e.preventDefault();
		const nameInput = document.getElementById("name");
		const mobileInput = document.getElementById("mobile");

		if (!nameInput || !mobileInput) {
			return;
		}

		const name = (nameInput as HTMLInputElement).value;
		const mobile = (mobileInput as HTMLInputElement).value;

		if (!name) {
			alert("Name is required");
			return;
		}

		if (!mobile) {
			alert("Mobile is required");
			return;
		}

		setsubmitting(true);
		try {
			const body = { name: e.target.name.value, mobile: e.target.mobile.value };
			await fetch(`/api/user`, {
				method: "POST",
				headers: { "Content-Type": "application/json" },
				body: JSON.stringify(body),
			});
		} catch (error) {
			console.error(error);
		}
		setsubmitting(false);
		clearInputFields();
		router.refresh();
	};

	return (
		<div className="bg-white/30 p-12 shadow-xl ring-1 ring-gray-900/5 rounded-lg backdrop-blur-lg max-w-xl mx-auto w-full">
			<div className="flex justify-between items-center mb-4">
				<div className="space-y-1">
					<h2 className="text-xl font-semibold">Add user to DB</h2>
					<div>
						<form onSubmit={submitData}>
							<div
								style={{
									display: "flex",
									alignItems: "center",
									flexDirection: "row",
								}}
							>
								<div
									style={{
										display: "flex",
										alignItems: "center",
									}}
								>
									<label
										style={{
											display: "flex",
											alignItems: "center",
										}}
										htmlFor="name"
										className="text-md p-2"
									>
										Name
									</label>
									<input
										style={{
											width: "100%",
											height: "56px",
											position: "relative",
											padding: "0px 16px",
											border: "none",
											borderRadius: "4px",
											fontSize: "16px",
											fontWeight: "400",
											lineHeight: "normal",
											backgroundColor: "white",
											color: "#282828",
											outline: "none",
											boxShadow: "0px 4px 20px 0px transparent",
											transition:
												"0.3s background-color ease-in-out, 0.3s box-shadow ease-in-out, 0.1s padding ease-in-out",
										}}
										className="text-md p-2"
										type="text"
										id="name"
										name="name"
										// value={formData.name}
										// onChange={handleChange}
									/>
									{/* <span className="error">{errors.name}</span> */}
								</div>
								<div
									style={{
										display: "flex",
										alignItems: "center",
										marginBottom: "10px",
									}}
								>
									<label htmlFor="mobile" className="text-md p-2">
										Mobile Number
									</label>
									<input
										style={{
											width: "100%",
											height: "56px",
											position: "relative",
											padding: "0px 16px",
											border: "none",
											borderRadius: "4px",
											fontSize: "16px",
											fontWeight: "400",
											lineHeight: "normal",
											backgroundColor: "white",
											color: "#282828",
											outline: "none",
											boxShadow: "0px 4px 20px 0px transparent",
											transition:
												"0.3s background-color ease-in-out, 0.3s box-shadow ease-in-out, 0.1s padding ease-in-out",
										}}
										className="text-md p-2"
										type="number"
										id="mobile"
										name="mobile"
										// value={formData.mobile}
										// onChange={handleChange}
									/>
									{/* <span className="error">{errors.mobile}</span> */}
								</div>
							</div>
							{submitting ? (
								<button disabled>Submitting ...</button>
							) : (
								<button type="submit" className="text-md p-2 pt-10 ">
									Submit
								</button>
							)}
						</form>
					</div>
				</div>
			</div>
			<div className="divide-y divide-gray-900/5"></div>
		</div>
	);
}
