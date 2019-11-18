'use strict';

(function (angular) {
    'use strict';

    adminControllers.controller('ProductCategoryListController', ProductCategoryListController)
        .factory('ProductCategoryList', ProductCategoryListFactory);

    ProductCategoryListController.$inject = ['$scope', '$http', '$q', '$location', '$resource', 'DTOptionsBuilder', 'ProductCategoryList'];

    function ProductCategoryListController($scope, $http, $q, $location, $resource, DTOptionsBuilder, ProductCategoryList) {
        var vm = this;
        
        if (localStorage.getItem("ADMIN_USER_ROLE") !== 'SUPER_USER' && localStorage.getItem("ADMIN_USER_ROLE") !== 'EDITOR') {
    		$location.path('/users/login');
    		return;
    	}
        
        vm.dtOptions = DTOptionsBuilder.newOptions()
            .withPaginationType('full_numbers')
			.withDisplayLength(100)
			.withOption("bAutoWidth", false)
			.withOption('searchable', false)
        
        $scope.host = location.host;
        $scope.pathName = location.pathname;


        ProductCategoryList.getProductCategoryLists().then(function(ProductCategoryLists) {
        	vm.myProductCategoryLists = ProductCategoryLists;
        });
    }

    ProductCategoryListFactory.$inject = ["$http"];
  
    function ProductCategoryListFactory($http) {
    	return {
    		getProductCategoryLists: function() {
    			var pathName = location.pathname;
    			return $http.get(pathName + 'api/v1/product/category/list').then(function(response) {
    				return response.data.data;
    			});
    		}
    	};
    }
}(angular));





