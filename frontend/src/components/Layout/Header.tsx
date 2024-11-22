import React from 'react';
import { useAuth } from '../../context/AuthContext';
import {
  ArrowRightOnRectangleIcon,
  UserCircleIcon,
} from '@heroicons/react/24/outline';

const Header: React.FC = () => {
  const { user, logout } = useAuth();

  return (
    <header className="sticky top-0 z-10 bg-white border-b border-gray-200 shadow-sm">
      <div className="flex h-16 items-center justify-between px-6">
        <div className="flex items-center">
          <h1 className="text-lg font-semibold text-gray-800">
            Inventory Management System
          </h1>
        </div>

        <div className="flex items-center space-x-4">
          <div className="flex items-center text-sm text-gray-600">
            <UserCircleIcon className="h-5 w-5 mr-1.5 text-gray-400" />
            <span className="font-medium">{user?.username}</span>
            <span className="ml-2 px-2 py-0.5 text-xs rounded-full bg-primary-100 text-primary-700 font-medium">
              {user?.role}
            </span>
          </div>

          <button
            onClick={logout}
            className="flex items-center text-sm text-gray-500 hover:text-red-600 transition-colors"
          >
            <ArrowRightOnRectangleIcon className="h-5 w-5 mr-1" />
            Logout
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;
