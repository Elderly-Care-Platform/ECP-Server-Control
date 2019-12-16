adminControllers.controller('EventController', [ '$scope',
		'$routeParams', '$location','$http',
		function($scope, $routeParams, $location, $http) {
			$scope.error = false;
			var id = $routeParams.eventId;
			$scope.event = {};
			$scope.event.id = id;
			$http.get("api/v1/event?eventId="+id).success(
					function(response) {
						response = response.data;
						if(response != null && response !== ""){
							$scope.event = response;
						}else{
							$scope.error = true;
							$scope.errorMessage = "No Event found";
						}
					}, function(errorResponse) {
						if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
							$location.path('/users/login');
							 return;
				        }
					});
			
			$scope.editEvent = function(){
				$http.put("api/v1/event/",$scope.event).success(function(res){
					toastr.success('Event submitted successfully');
					$location.path('/events');
				}).error(function(errorResponse){
					if(errorResponse.data && errorResponse.data.error && errorResponse.data.error.errorCode === 3002){
						$location.path('/users/login');
						 return;
			        }
					$scope.error = true;
					$scope.errorMessage = "Error occured in saving the current event.";
				})
			}
		} ]);