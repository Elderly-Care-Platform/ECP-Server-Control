adminControllers.controller('ProductCategoryController', [ '$scope',
	'$routeParams', '$location','$http',
	function($scope, $routeParams, $location, $http) {
		$scope.error = false;
		var id = $routeParams.productCategoryId;
		$scope.productCategory = {
			id: "",
			name: ""
		};
		if(id != "new"){
			$scope.productCategory.id = id;
			$http.get("api/v1/product/category?productCategoryId="+id).success(
				function(response) {
					response = response.data;
					if(response != null && response !== ""){
						$scope.productCategory = response;
					}else{
						$scope.error = true;
						$scope.errorMessage = "No Product Category found";
					}
				}, function(errorResponse) {
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
					}
				});
		}

		$scope.editProductCategory = function(){
			var prod = JSON.parse(JSON.stringify($scope.productCategory));
			$http.put("api/v1/product/category/",prod).success(function(res){
				toastr.success('Product category submitted successfully');
				$location.path('/products/category/list');
			}).error(function(errorResponse){
				if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
					$location.path('/users/login');
					 return;
				}
				$scope.error = true;
				$scope.errorMessage = "Error occured in saving the current productCategory.";
			})
		}

		$scope.addProductCategory = function(){
			var prod = JSON.parse(JSON.stringify($scope.productCategory));
			delete prod.id;
			$http.post("api/v1/product/category/",prod).success(function(res){
				toastr.success('Product Category submitted successfully');
				$location.path('/products/category/list');
			}).error(function(errorResponse){
				if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
					$location.path('/users/login');
					 return;
				}
				$scope.error = true;
				$scope.errorMessage = "Error occured in saving the current product category.";
			})
		}
	}
]);
