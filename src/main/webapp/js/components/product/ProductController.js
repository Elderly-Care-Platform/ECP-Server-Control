adminControllers.controller('ProductController', [ '$scope',
		'$routeParams', '$location','$http',
		function($scope, $routeParams, $location, $http) {
			$scope.error = false;
			var id = $routeParams.productId;
			$scope.product = {};
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
			
			$scope.editProduct = function(){
				$http.put("api/v1/product/",$scope.product).success(function(res){
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