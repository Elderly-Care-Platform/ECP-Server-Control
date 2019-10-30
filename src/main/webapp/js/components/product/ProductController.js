adminControllers.controller('ProductController', [ '$scope',
		'$routeParams', '$location','$http',
		function($scope, $routeParams, $location, $http) {
			$scope.error = false;
			var id = $routeParams.productId;
			$scope.product = {
				id: "",
				name: "",
				shortDescription: "",
				description:"",
				isFeatured: false,
				rating: 0,
				reviews: 0,
				price: 0,
				status: 0,
				buyLink: "",
				buyFrom: "",
				images:[],
				productCategory:""
			};
			$scope.categories = [];
			$http.get("api/v1/product/category/list").success(
				function(response) {
					response = response.data;
					if(response != null && response !== ""){
						$scope.categories = response;
					}else{
						$scope.error = true;
						$scope.errorMessage = "No Categories found";
					}
				}, function(errorResponse) {
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
					}
				});
			if(id != "new"){
				$scope.product.id = id;
				$http.get("api/v1/product?productId="+id).success(
					function(response) {
						response = response.data;
						if(response != null && response !== ""){
							$scope.product = response;
						}else{
							$scope.error = true;
							$scope.errorMessage = "No Product found";
						}
					}, function(errorResponse) {
						if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
							$location.path('/users/login');
							 return;
				        }
					});
			}
			
			$scope.editProduct = function(){
				var prod = JSON.parse(JSON.stringify($scope.product));
				if(prod.images){
					prod.images = prod.images.split(',');
				}
				$http.put("api/v1/product/",prod).success(function(res){
					toastr.success('Product submitted successfully');
					$location.path('/products');
				}).error(function(errorResponse){
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
			        }
					$scope.error = true;
					$scope.errorMessage = "Error occured in saving the current product.";
				})
			}

			$scope.addProduct = function(){
				var prod = JSON.parse(JSON.stringify($scope.product));
				delete prod.id;
				if(prod.images){
					prod.images = prod.images.split(',');
				}
				$http.post("api/v1/product/",prod).success(function(res){
					toastr.success('Product submitted successfully');
					$location.path('/products');
				}).error(function(errorResponse){
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
			        }
					$scope.error = true;
					$scope.errorMessage = "Error occured in saving the current product.";
				})
			}
		} ]);