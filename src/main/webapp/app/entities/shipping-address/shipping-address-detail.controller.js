(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('ShippingAddressDetailController', ShippingAddressDetailController);

    ShippingAddressDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ShippingAddress', 'User'];

    function ShippingAddressDetailController($scope, $rootScope, $stateParams, previousState, entity, ShippingAddress, User) {
        var vm = this;

        vm.shippingAddress = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('salesOrderApp:shippingAddressUpdate', function(event, result) {
            vm.shippingAddress = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
