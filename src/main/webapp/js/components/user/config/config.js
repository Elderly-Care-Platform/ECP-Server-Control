adminControllers.controller('UserRoleController', [ '$scope', function($scope) {
	$scope.userRoleIds = [ "SUPER_USER", "EDITOR", "USER", "EXPERT" ];
} ]);

adminControllers.controller('UserStateController', [ '$scope',
		function($scope) {
			$scope.isActives = [ "In-Active", "Active" ];
		} ]);
